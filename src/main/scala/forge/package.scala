import play.api.libs.json._
import ammonite.ops.{Bytes, Path}
import coursier.Dependency
import forge.util.Args
package object forge {

  val T = Target
  type T[T] = Target[T]
  def zipMap[R]()(f: () => R) = T(f())
  def zipMap[A, R](a: T[A])(f: A => R) = a.map(f)
  def zipMap[A, B, R](a: T[A], b: T[B])(f: (A, B) => R) = zip(a, b).map(f.tupled)
  def zipMap[A, B, C, R](a: T[A], b: T[B], c: T[C])(f: (A, B, C) => R) = zip(a, b, c).map(f.tupled)
  def zipMap[A, B, C, D, R](a: T[A], b: T[B], c: T[C], d: T[D])(f: (A, B, C, D) => R) = zip(a, b, c, d).map(f.tupled)
  def zipMap[A, B, C, D, E, R](a: T[A], b: T[B], c: T[C], d: T[D], e: T[E])(f: (A, B, C, D, E) => R) = zip(a, b, c, d, e).map(f.tupled)
  def zip() = T(())
  def zip[A](a: T[A]) = a.map(Tuple1(_))
  def zip[A, B](a: T[A], b: T[B]) = a.zip(b)
  def zip[A, B, C](a: T[A], b: T[B], c: T[C]) = new T[(A, B, C)]{
    val inputs = Seq(a, b, c)
    def evaluate(args: Args) = (args[A](0), args[B](1), args[C](2))
  }
  def zip[A, B, C, D](a: T[A], b: T[B], c: T[C], d: T[D]) = new T[(A, B, C, D)]{
    val inputs = Seq(a, b, c, d)
    def evaluate(args: Args) = (args[A](0), args[B](1), args[C](2), args[D](3))
  }
  def zip[A, B, C, D, E](a: T[A], b: T[B], c: T[C], d: T[D], e: T[E]) = new T[(A, B, C, D, E)]{
    val inputs = Seq(a, b, c, d, e)
    def evaluate(args: Args) = (args[A](0), args[B](1), args[C](2), args[D](3), args[E](4))
  }
  implicit object pathFormat extends Format[ammonite.ops.Path]{
    def reads(json: JsValue) = json match{
      case JsString(v) => JsSuccess(Path(v))
      case _ => JsError("Paths must be a String")
    }
    def writes(o: Path) = JsString(o.toString)
  }

  implicit object bytesFormat extends Format[Bytes]{
    def reads(json: JsValue) = json match{
      case JsString(v) => JsSuccess(
        new Bytes(javax.xml.bind.DatatypeConverter.parseBase64Binary(v))
      )
      case _ => JsError("Bytes must be a String")
    }
    def writes(o: Bytes) = {
      JsString(javax.xml.bind.DatatypeConverter.printBase64Binary(o.array))
    }
  }

  implicit def EitherFormat[T: Format, V: Format] = new Format[Either[T, V]]{
    def reads(json: JsValue) = json match{
      case JsObject(struct) =>
        (struct.get("type"), struct.get("value")) match{
          case (Some(JsString("Left")), Some(v)) => implicitly[Reads[T]].reads(v).map(Left(_))
          case (Some(JsString("Right")), Some(v)) => implicitly[Reads[V]].reads(v).map(Right(_))
          case _ => JsError("Either object layout is unknown")
        }
      case _ => JsError("Either must be an Object")
    }
    def writes(o: Either[T, V]) = o match{
      case Left(v) => Json.obj("type" -> "Left", "value" -> implicitly[Writes[T]].writes(v))
      case Right(v) => Json.obj("type" -> "Right", "value" -> implicitly[Writes[V]].writes(v))
    }
  }

  implicit val crFormat: Format[ammonite.ops.CommandResult] = Json.format
  implicit val modFormat: Format[coursier.Module] = Json.format
  // https://github.com/playframework/play-json/issues/120
  // implicit val depFormat: Format[coursier.Dependency] = Json.format
  implicit val depFormat: Format[coursier.Dependency] =  new Format[coursier.Dependency] {
    def writes(o: Dependency) = {
      Json.obj(
        "module" -> Json.toJson(o.module),
        "version" -> Json.toJson(o.version),
        "configuration" -> Json.toJson(o.configuration),
        "exclusions" -> Json.toJson(o.exclusions),
        "attributes" -> Json.toJson(o.attributes),
        "optional" -> Json.toJson(o.optional),
        "transitive" -> Json.toJson(o.transitive)
      )
    }

    def reads(json: JsValue) = json match{
      case x: JsObject =>
        JsSuccess(coursier.Dependency(
          Json.fromJson[coursier.Module](x.value("module")).get,
          Json.fromJson[String](x.value("version")).get,
          Json.fromJson[String](x.value("configuration")).get,
          Json.fromJson[coursier.Attributes](x.value("attributes")).get,
          Json.fromJson[Set[(String, String)]](x.value("exclusions")).get,
          Json.fromJson[Boolean](x.value("optional")).get,
          Json.fromJson[Boolean](x.value("transitive")).get
        ))

      case _ => JsError("Dep must be an object")
    }
  }
  implicit val attrFormat: Format[coursier.Attributes] = Json.format
}
