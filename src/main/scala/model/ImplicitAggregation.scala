package model

import model.ImplicitAggregation._

object ImplicitAggregation {

  def canAggregate: Concept => Boolean = q => allLevels(q).nonEmpty && allMeasures(q).nonEmpty && !allFeatures(q).exists(f => f match {
    case _: Measure => false
    case _: IdFeature => false
    case _ => true
  })

  def extractGroupByClauses: Concept => Set[Level] = allLevels

  def extractAggregationClauses: (Concept, Concept) => Set[(Measure,AggregatingFunction)] = ???

  def parseGBClauses: (Concept,Concept => Set[Level]) => Set[String] = ???

  def parseAggregationClauses: (Concept, Concept, (Concept, Concept) => Set[(Measure,AggregatingFunction)]) => Set[(Measure,AggregatingFunction)] = ???

  def makeView: Unit => String = ???

  def makeSqlQuery: (Concept, Concept,
    (Concept, Concept) => Set[(Measure,AggregatingFunction)],(Concept,Concept => Set[Level]) => Set[String],
    (Concept,Concept => Set[Level]) => Set[String],(Concept, Concept, (Concept, Concept) => Set[(Measure,AggregatingFunction)]) => Set[(Measure,AggregatingFunction)],
    Unit => String
    ) => String = ???

  def allConcept(query: Concept): Set[Concept] =
    if(query.linkedConcepts.isEmpty) Set(query) else query.linkedConcepts.flatMap(c => allConcept(c._2)) + query

  def allLevels(query: Concept): Set[Level] =
    query match {
      case l: Level => if (query.linkedConcepts.nonEmpty) query.linkedConcepts.flatMap(c => allLevels(c._2)) + l else Set(l)
      case _ => if(query.linkedConcepts.nonEmpty) query.linkedConcepts.flatMap(c => allLevels(c._2)) else Set.empty
    }

  def allFeatures(query: Concept): Set[Feature] =
    query.linkedFeatures.map(_._2) ++ {
      if (query.linkedConcepts.nonEmpty) query.linkedConcepts.flatMap(c => allFeatures(c._2)) else Set.empty
    }

  def allMeasures(query: Concept): Set[Measure] =
    query.linkedFeatures.collect(f => f._2 match {
      case m:Measure => m
    }) ++ {
      if (query.linkedConcepts.nonEmpty) query.linkedConcepts.flatMap(c => allMeasures(c._2)) else Set.empty
    }

}

object TestAgg extends App{
  // Features
  val CITY = IdFeature("CITY")
  val REGION = IdFeature("REGION")
  val COUNTRY = IdFeature("COUNTRY")
  val REVENUE = Measure("REVENUE")

  // Graph hierarchy
  val q =
    Concept("Sales")
      .hasFeature{REVENUE}
      .->("location"){
        Level("City")
          .hasFeature{CITY}
          .partOf{
            Level("Region")
              .hasFeature{REGION}
              .partOf{
                Level("Country")
                  .hasFeature{COUNTRY}
              }
          }
      }

  println(
    canAggregate(q)
  )

}
