package model

import java.io.BufferedWriter

// Model definition

trait GraphComponent {
  def name:String
}
case class Edge(name:String) extends GraphComponent

trait Feature extends GraphComponent
case class IdFeature(name:String) extends Feature
case class GenericFeature(name:String) extends Feature

trait Concept extends GraphComponent {
  def linkedConcepts:Set[(Edge, Concept)]
  def linkedFeatures:Set[(Edge, Feature)]

  def hasFeature(feature: Feature):Concept
  def hasConcept(label:String)(concept: Concept):Concept
  def ->(label:String)(concept: Concept):Concept
  def ->(graphComponent: Feature):Concept

  def partOf(concept: Concept):Concept

}

case class ConceptImpl(
                        name:String,linkedConcepts: Set[(Edge, Concept)],linkedFeatures: Set[(Edge, Feature)]
                      ) extends Concept {

  override def hasFeature(feature: Feature): Concept =
    ConceptImpl(
      this.name,
      this.linkedConcepts,
      this.linkedFeatures + Tuple2(Edge("hasFeature"),feature)
    )

  override def hasConcept(label: String)(concept: Concept): Concept =
    ConceptImpl(
      this.name,
      this.linkedConcepts + Tuple2(Edge(label),concept),
      this.linkedFeatures
    )

  def ->(feature: Feature): Concept = hasFeature(feature)

  def ->(label: String)(concept: Concept): Concept = hasConcept(label)(concept)

  override def partOf(concept: Concept): Concept = hasConcept("partOf")(concept)
}

trait Attribute extends GraphComponent {
  def sameAs:Option[Feature]
  def sameAs(feature: Feature):Attribute
}

object Attribute {
  def apply(name: String): Attribute = AttributeImpl(name,None)
}

case class AttributeImpl (name: String, sameAs: Option[Feature]) extends Attribute {
  override def sameAs(feature: Feature): Attribute = AttributeImpl(this.name,Some(feature))
}

trait Wrapper extends GraphComponent {
  def attributes: Set[Attribute]
  def hasAttribute(attribute: Attribute):Wrapper
  def -> (attribute: Attribute):Wrapper
}

case class WrapperImpl(name: String,attributes: Set[Attribute]) extends Wrapper {
  override def hasAttribute(attribute: Attribute): Wrapper = WrapperImpl(this.name, this.attributes + attribute)
  override def ->(attribute: Attribute): Wrapper = hasAttribute(attribute)
}

// Operations definition

object Concept {

  def linkedConceptsWithLabel(concept: Concept)(label: String):Set[Concept] =
    concept.linkedConcepts.filter(_._1.name == label).map(_._2)

  def linkedFeatureName(concept: Concept)(name: String):Feature =
    concept.linkedFeatures.filter(_._2.name == name).map(_._2).head

  /**
   * Doesn't work with cyclic graphs, needs to be fixed
   */
  def generateGlobalGraphFile(concept: Concept)(f: BufferedWriter): Unit = {
      // model.Concept definition
      f.write("s:" + concept.name + " rdf:type G:Concept\n")
      // Features
      concept.linkedFeatures.foreach(feature => {
        // Type feature
        f.write("s:" + feature._2.name + " rdf:type G:Feature\n")
        // model.Concept hasFeature model.Feature *** [MAPPING_LINE]
        f.write("s:" + concept.name + " G:" + feature._1.name + " s:" + feature._2.name + "\n")
        // Identifiers *** [MAPPING_LINE]
        feature._2 match {
          case IdFeature(name) =>
            f.write("s:" + name + " rdfs:subClassOf sc:identifier\n")
          case _ =>
        }
      })
      // Edges pointed to other concepts *** [MAPPING_LINE]
      concept.linkedConcepts.foreach(edge => {
        f.write("s:" + concept.name + " s:" + edge._1.name + " s:" + edge._2.name + "\n")
      })
      // rec
      if (concept.linkedConcepts.nonEmpty) {
        concept.linkedConcepts.map(_._2).foreach(generateGlobalGraphFile(_)(f))
      }
    }

  def apply(name:String): Concept = ConceptImpl(name,Set.empty,Set.empty)
}

object Wrapper {

  private type WrapperWriter = (Wrapper,BufferedWriter,String) => Unit

  object GenerateMappingsFile extends WrapperWriter {
    override def apply(wrapper: Wrapper, f: BufferedWriter, path:String): Unit = {
      f.write("s:" + wrapper.name + "-\n")
    }
  }

  object GenerateCsv extends WrapperWriter {
    override def apply(wrapper: Wrapper, f: BufferedWriter, path:String): Unit = {
      wrapper.attributes.foreach(a => f.write(a.name + ","))
    }
  }

  object GenerateWrapperFile extends WrapperWriter {
    override def apply(wrapper: Wrapper, f: BufferedWriter, path:String): Unit = {
      f.write("https://serginf.github.io/" + wrapper.name + "," + path + wrapper.name + ".csv\n")
    }
  }

  object GenerateSourceGraphFile extends WrapperWriter {
    override def apply(wrapper: Wrapper, f: BufferedWriter, path:String): Unit = {
      // model.Wrapper
      f.write("s:" + wrapper.name + " rdf:type S:Wrapper\n")
      // Attributes
      wrapper.attributes.foreach(a => {
        f.write("s:" + a.name + " rdf:type S:Attribute\n")
        f.write("s:" + wrapper.name + " S:hasAttribute " + "s:" + a.name + "\n")
        a.sameAs match {
          case Some(attr) => f.write("s:" + a.name + " owl:sameAs " + "s:" + attr.name + "\n")
          case None =>
        }
      })
    }
  }

  def apply(name:String): Wrapper = WrapperImpl("Wrapper" + name,Set.empty)

}