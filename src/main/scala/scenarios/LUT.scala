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
      .->(REVENUE)
      .->("location"){
        Concept("City")
          .->(CITY)
          .partOf{
            Concept("Region")
              .->(REGION)
              .partOf{
                Concept("Country")
                  .->(COUNTRY)
              }
          }
      }

  // Wrappers
  val w1 =
    Wrapper("W1")
      .->(Attribute("country") sameAs COUNTRY)
      .->(Attribute("revenue") sameAs REVENUE)

  val w2 =
    Wrapper("W2")
      .->(Attribute("region") sameAs REGION)
      .->(Attribute("revenue2") sameAs REVENUE)

  val w3 =
    Wrapper("LUT")
      .->(Attribute("region1") sameAs REGION)
      .->(Attribute("country1") sameAs COUNTRY)

  // WRITERS
  Utils.generateAllFiles(Set(Sales),Set(w1,w2,w3))(configurationFilePath)
}
