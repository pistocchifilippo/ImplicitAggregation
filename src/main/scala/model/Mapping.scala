package model

trait Mapping {
  def features:Set[(Feature,Int)]
  def concepts:Set[(Concept,Concept,Int)]
}

case class MappingImpl(
                        features:Set[(Feature,Int)],
                        concepts:Set[(Concept,Concept,Int)]
                      ) extends Mapping
