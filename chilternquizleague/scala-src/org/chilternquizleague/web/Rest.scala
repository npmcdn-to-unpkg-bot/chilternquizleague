package org.chilternquizleague.web

import java.util.logging.Level
import java.util.logging.Logger
import java.util.{List => JList}
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.immutable.List
import scala.util.control.Exception.catching
import org.chilternquizleague.domain.BaseEntity
import org.chilternquizleague.domain.Competition
import org.chilternquizleague.domain.CompetitionType
import org.chilternquizleague.domain.Fixtures
import org.chilternquizleague.domain.GlobalApplicationData
import org.chilternquizleague.domain.Results
import org.chilternquizleague.domain.Season
import org.chilternquizleague.domain.Team
import org.chilternquizleague.domain.TeamCompetition
import org.chilternquizleague.domain.Text
import org.chilternquizleague.domain.User
import org.chilternquizleague.domain.individuals.IndividualQuiz
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.results.ResultHandler
import org.chilternquizleague.util.HttpUtils.RequestImprovements
import org.chilternquizleague.util.StringUtils.StringImprovements
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.googlecode.objectify.Key
import com.googlecode.objectify.ObjectifyService.ofy
import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.chilternquizleague.util.Storage.{entity => entityByKey}
import org.chilternquizleague.util.Storage.entityList
import org.chilternquizleague.util.Storage.save
import java.util.ArrayList
import scala.collection.immutable.Iterable
import java.util.Date
import com.googlecode.objectify.Ref
import com.googlecode.objectify.util.jackson.RefSerializer
import com.googlecode.objectify.util.jackson.RefDeserializer
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.core.SerializableString
import com.fasterxml.jackson.core.io.SerializedString
import com.fasterxml.jackson.databind.JsonNode
import java.io.StringWriter
import com.google.api.client.util.StringUtils
import org.apache.commons.io.IOUtils


trait BaseRest extends HttpServlet {

  class ScalaIterableSerialiser extends JsonSerializer[Iterable[Any]]{
    override def serialize(list:Iterable[Any], gen: JsonGenerator, prov: SerializerProvider):Unit = {
      gen writeStartArray;
      
      list foreach {gen.writeObject(_)}
      
      gen writeEndArray()
      
    }
  }
  
  val LOG: Logger = Logger.getLogger(classOf[BaseRest].getName());

  def aliases: Map[String, String]
  val packages: List[Package] = List(classOf[BaseEntity].getPackage(), classOf[IndividualQuiz].getPackage());
  val objectMapper: ObjectMapper = new ObjectMapper
  def parts(req: HttpServletRequest) = req.getPathInfo().split("\\/").tail;
  def entityFilter[T <: BaseEntity]: T => Boolean

  def entityName(head: String) = {
    val stripped = head.replace("-list", "")
    aliases.getOrElse(stripped, stripped)
  }

  def classFromPart[T](part: String) = {

    val className = part.substring(0, 1).toUpperCase() + part.substring(1)
    def fun(c: Option[Class[T]], p: Package): Option[Class[T]] = {
    	import scala.util.control.Exception._

      if (c.isDefined) c else catching(classOf[ClassNotFoundException]) opt Class.forName(p.getName() + "." + className).asInstanceOf[Class[T]]
    }
    packages.foldLeft(Option[Class[T]](null))(fun)
  }

  def handleEntities(parts: Seq[String], head: String): Option[_] = {

    val entName = entityName(head)
    head match {

      case e if e.`contains`("-list") => makeEntityList(entName).map(new ArrayList(_))
      case _ => entityByParam(parts.tail.head, entName)
    }

  }

  def makeEntityList[T <: BaseEntity](entityName: String):Option[List[T]] = classFromPart(entityName) flatMap { c:Class[T] => makeEntityList(c) }
  def makeEntityList[T <: BaseEntity](c:Class[T]):Option[List[T]] = Some(entityList(c).filter(entityFilter[T]))

  def entityByParam[T](idPart: String, entityName: String): Option[T] = classFromPart[T](entityName) flatMap { clazz: Class[T] => entityByKey(idPart.toLongOpt, clazz) }

  def saveUpdate[T <: BaseEntity](req: HttpServletRequest, entityName: String): T = {

    val retval = classFromPart[T](entityName) map { clazz =>
      ofy.load.key(save(objectMapper.readValue(req.getReader(), clazz))).now
    }
    retval.getOrElse(throw new ClassNotFoundException(entityName))
  }

  def logJson[T](things: T, message: String = "") = {
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine(message + "\n" + things.getClass.getName() + " : " +objectMapper.writeValueAsString(things));
    }

    things
  }
  
   
}

class EntityService extends BaseRest {

  override val aliases = Map(("text", "globalText"), ("global", "GlobalApplicationData"))
  override def entityFilter[T] = { _ => true }

  override def init(config: ServletConfig) = {
    val module = new SimpleModule
    module.addSerializer(classOf[Ref[_]], new RefSerializer)
    module.addDeserializer(classOf[Ref[_]], new RefDeserializer())
    module.addSerializer(classOf[Iterable[Any]], new ScalaIterableSerialiser)
    objectMapper registerModule module
  }
  
  
  class RefSerializer extends JsonSerializer[Ref[_]]{
    override def serialize(ref: Ref[_], gen: JsonGenerator, prov: SerializerProvider) = gen.writeObject(ref.get)
  }      

   class RefDeserializer extends JsonDeserializer[Ref[_]]{
     override def deserialize(parser:JsonParser, context:DeserializationContext):Ref[_] = {
       val node:JsonNode = parser.getCodec().readTree(parser);
       
         val className = node.get("refClass").asText
         val opt = classFromPart[BaseEntity](className)
         
         val remote = for{
           clazz <- opt
         }
         yield{
           parser.getCodec().treeToValue(node, clazz)
         }
        
         Ref.create(ofy.save.entity(remote.get).now())
     }	
  }
  
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val item = head match {

      case "global" => entityByKey(Application.globalApplicationDataId, classOf[GlobalApplicationData])
      case "competitionType-list" => Some(CompetitionTypeView.list)
      case _ => handleEntities(bits, head)

    }

    item.foreach(a => objectMapper.writeValue(resp.getWriter, logJson(a, "writing:")))

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    
    val item:AnyRef = saveUpdate(req, entityName(parts(req).head))

    objectMapper.writeValue(resp.getWriter, logJson(item, "out:"))
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse) = doPost(req, resp)

}

class ViewService extends BaseRest {

  override val aliases = Map[String, String]()
  override def entityFilter[T <: BaseEntity] = { !_.retired }

  class UserSerializer extends JsonSerializer[User] {
    override def serialize(user: User, gen: JsonGenerator, prov: SerializerProvider) = {}
  }
  class TextSerializer extends JsonSerializer[Text] {
    override def serialize(text: Text, gen: JsonGenerator, prov: SerializerProvider) = {
      gen writeStartObject;
      gen writeNullField "text"
      gen writeEndObject
    }

  }

  override def init(config: ServletConfig) = {
    val module = new SimpleModule
    module.addSerializer(classOf[User], new UserSerializer)
    module.addSerializer(classOf[Text], new TextSerializer)
    module.addSerializer(classOf[Iterable[Any]], new ScalaIterableSerialiser)
    module.addSerializer(classOf[Ref[_]], new RefSerializer)
    module.addDeserializer(classOf[Ref[_]], new RefDeserializer)
    objectMapper registerModule module
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head

    val item: Option[_] = head match {

      case a if a.contains("globaldata") => entityByKey(Application.globalApplicationDataId, classOf[GlobalApplicationData]).map(new GlobalApplicationDataView(_))
      case a if a.contains("leaguetable") => currentLeagueTable(req, CompetitionType.LEAGUE)
      case a if a.contains("beertable") => currentLeagueTable(req, CompetitionType.BEER)
      case a if a.contains("season-views") => seasons
      case a if a.contains("team-extras") => teamExtras(req)
      case a if a.contains("all-results") => allResults(req)
      case a if a.contains("all-fixtures") => allFixtures(req)
      case a if a.contains("competition-results") => competitionResults(req)
      case a if a.contains("competition-fixtures") => competitionFixtures(req)
      case a if a.contains("fixtures-for-email") => fixturesForEmail(req)
      case a if a.contains("competitions-view") => competitionsForSeason(req)
      case a if a.contains("text") => textForName(req)
      case a if a.contains("reports") => resultReports(req)
      case _ => handleEntities(bits, head)

    }

    item foreach { a => objectMapper.writeValue(resp.getWriter, logJson(a, "viewService writing:")) }

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {

    val ret: Option[_] = req.getPathInfo() match {

      case a if a.endsWith("submit-results") => submitResults(req)
      case a if a.endsWith("submit-contact") => submitContact(req)
      case _ => None

    }

    ret.foreach(r => objectMapper.writeValue(resp.getWriter, r))

  }
  
  def seasons():Option[List[SeasonView]] = {makeEntityList(classOf[Season]) map { _ map {new SeasonView(_)}}}

  def submitResults(req: HttpServletRequest) = {

    val submissions = objectMapper.readValue(req.getReader(), classOf[Array[ResultSubmission]]);

    submissions.foreach(sub => ResultHandler(sub.result, sub.email, sub.seasonId, sub.competitionType))

    None
  }

  def submitContact(req: HttpServletRequest) = {
    val submission = objectMapper.readValue(
      req.getReader, classOf[ContactSubmission]);

    EmailSender(submission.sender, submission.recipient, submission.text);
    None
  }

  def currentLeagueTable(req: HttpServletRequest, compType: CompetitionType):Option[LeagueTableView] = entityByKey(idParam(req), classOf[Season]) map (a => new LeagueTableView(a, compType))

  def teamExtras(req: HttpServletRequest): Option[TeamExtras] = {

    val teamId = idParam(req, "teamId")
    for{
      t <- entityByKey(teamId, classOf[Team])
      s <- entityByKey(idParam(req, "seasonId"), classOf[Season])
    }
    yield{
      new TeamExtras(t, teamFixtures(teamId, s), teamResults(teamId, s))
    }
 }

  def teamFixtures(teamId: Option[Long], season: Season, limit:Int = 20000, filter:Fixtures => Boolean = {_ => true}): List[Fixtures] = {

    val competitions = season.teamCompetitions

    def flatMapFixtures(f: Fixtures): List[Fixtures] = {

      val newFix = Fixtures(f);
      newFix.fixtures = f.fixtures.toList filter { f => (teamId contains f.home.id) || (teamId contains f.away.id) }

      if (newFix.fixtures.isEmpty) Nil else List(newFix)
    }

    competitions filter { _ != null } filter { _.`type` != CompetitionType.BEER } flatMap { _.fixtures.map(_.get) filter filter flatMap flatMapFixtures } sortWith(_ .start before  _.start) slice(0, limit)


  }

  def teamResults(teamId: Option[Long], season: Season, limit:Int = 20000, filter:Results => Boolean = {_ => true}): List[Results] = {
    val competitions = season.teamCompetitions

    def flatMapResults(f: Results): List[Results] = {

      val newRes = Results(f);
      newRes.results = f.results filter { f => (teamId contains f.fixture.home.id) || (teamId contains f.fixture.away.id) }

      if (newRes.results.isEmpty) Nil else List(newRes)
    }

    competitions filter { _ != null } filter { _.`type` != CompetitionType.BEER } flatMap { _.results.map(_.get) filter filter flatMap flatMapResults } sortWith(_.date before _.date) slice(0,limit)

  }

  /**
   * Relies on http params id:Season and type:CompetitionType
   */
  private def teamCompetitionForSeason(req: HttpServletRequest):Option[TeamCompetition] = {
      val compType = req.parameter("type") map { t => CompetitionType.valueOf(t) }

    entityByKey(idParam(req), classOf[Season]) flatMap {
      s => compType map {
        t => s.competition(t).asInstanceOf[TeamCompetition] }}
  }
  
  def competitionResults(req: HttpServletRequest): Option[JList[Results]] = {

    teamCompetitionForSeason(req) map {a:TeamCompetition => a.results}
  }

  def competitionFixtures(req: HttpServletRequest): Option[JList[Fixtures]] = {

   teamCompetitionForSeason(req) map {a: TeamCompetition => a.fixtures}

  }

  def textForName(req: HttpServletRequest): Option[String] = {

    entityByKey(Application.globalApplicationDataId, classOf[GlobalApplicationData]).flatMap(g => { req.parameter("name") map { n => g.globalText.text(n) } })
  }

  def allResults(req: HttpServletRequest):Option[JList[_]] =
     entityByKey(idParam(req), classOf[Season]).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.results })

  def allFixtures(req: HttpServletRequest):Option[JList[_]] =
    entityByKey(idParam(req), classOf[Season]).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.fixtures })

    
  def fixturesForEmail(req: HttpServletRequest): Option[PreSubmissionView] = {

    for{
      e <- req.parameter("email").map(_.trim())
      t <- entityList(classOf[Team]).find(_.users.exists(_.email equalsIgnoreCase e))
      s <- entityByKey(idParam(req, "seasonId"), classOf[Season])
    }
    yield{
      new PreSubmissionView(t, teamFixtures(Some(t.id), s), teamResults(Some(t.id),s))
    } 
  }

  def competitionsForSeason(req: HttpServletRequest): Option[JList[CompetitionView]] =
    entityByKey(idParam(req), classOf[Season]).map(_.competitions.values.toList.map { a => new CompetitionView(a) })

  def resultReports(req: HttpServletRequest): Option[ResultsReportsView] = {
    for{
      t <- entityByKey(idParam(req, "homeTeamId"), classOf[Team])
      key <- req.parameter("resultsKey")
      r <- Option(ofy.load.key(Key.create(key)).now.asInstanceOf[Results])
      reps <- r.findRow(t)
    }
    yield{
      new ResultsReportsView(reps) 
    }
  }

  protected def idParam(req: HttpServletRequest, name: String = "id") = req parameter (name) flatMap { _ toLongOpt }

}