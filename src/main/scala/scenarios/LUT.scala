package scenarios

import model._

object LUT extends App {

//  val configurationFilePath = "/Users/filippo/Desktop/NextiaQR/src/test/resources/scenarios/LUT/"
  val configurationFilePath = "/Users/filippo/GraphBuilder/scenarios/LUT/"
  Utils.buildPath(configurationFilePath)

  // GRAPH DEFINITION

  // Features
  val CITY = IdFeature("CITY")
  val REGION = IdFeature("REGION")
  val COUNTRY = IdFeature("COUNTRY")
  val REVENUE = GenericFeature("REVENUE")

  // Graph hierarchy
  val Sales =
    Concept("Sales")
      .hasFeature{REVENUE}
      .->("location"){
        Concept("City")
          .hasFeature{CITY}
          .partOf{
            Concept("Region")
              .hasFeature{REGION}
              .partOf{
                Concept("Country")
                  .hasFeature{COUNTRY}
              }
          }
      }

  // Wrappers
  val w1 =
    Wrapper("W1")
      .hasAttribute{Attribute("country") sameAs COUNTRY}
      .hasAttribute{Attribute("revenue") sameAs REVENUE}

  val w2 =
    Wrapper("W2")
      .hasAttribute{Attribute("region") sameAs REGION}
      .hasAttribute{Attribute("revenue2") sameAs REVENUE}

  val w3 =
    Wrapper("LUT")
      .hasAttribute{Attribute("region1") sameAs REGION}
      .hasAttribute{Attribute("country1") sameAs COUNTRY}

  // WRITERS
  Utils.generateAllFiles(Set(Sales),Set(w1,w2,w3))(configurationFilePath)

}
