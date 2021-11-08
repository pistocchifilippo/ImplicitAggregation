package model

import java.io.{BufferedWriter, File, FileWriter}

object Utils {

  def buildPath(path:String): Boolean = {
    new File(path).mkdir()
  }

  def generateAllFiles(concepts: Set[Concept], wrappers: Set[Wrapper])(scenario: String): Unit = {
    println("Generating files in directory: " + scenario)
    // Writers
    val globalGraphWriter = new BufferedWriter(new FileWriter(scenario + "global_graph.txt"))
    val sourceGraphWriter = new BufferedWriter(new FileWriter(scenario + "source_graph.txt"))
    val wrapperWriter = new BufferedWriter(new FileWriter(scenario + "wrappers_files.txt"))
    val mappingWriter = new BufferedWriter(new FileWriter(scenario + "mappings.txt"))
    // Generating global graph files
    concepts.foreach(Concept.generateGlobalGraphFile(_)(globalGraphWriter))
    // generating files related to wrappers
    wrappers.foreach(w => {
      Wrapper.GenerateSourceGraphFile(w,sourceGraphWriter,scenario)
      Wrapper.GenerateMappingsFile(w,mappingWriter,scenario)
      Wrapper.GenerateWrapperFile(w,wrapperWriter,scenario)
      // Generating data files for each wrapper
      val csvWriter = new BufferedWriter(new FileWriter(scenario + w.name +".csv"))
      Wrapper.GenerateCsv(w,csvWriter,scenario)
      csvWriter.close()
    })
    // Closing
    globalGraphWriter.close()
    sourceGraphWriter.close()
    wrapperWriter.close()
    mappingWriter.close()
  }

  // generation of query file
  // generation of 2 prepared files that i should copy

}
