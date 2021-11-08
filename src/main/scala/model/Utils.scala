package model

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Files

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

    copyF(new File("configFiles/metamodel.txt"), scenario + "metamodel.txt")
    copyF(new File("configFiles/prefixes.txt"), scenario + "prefixes.txt")
    copyF(new File("configFiles/queries.txt"), scenario + "queries.txt")

  }

  def copyF(from: java.io.File, to: String) {
    val out = new BufferedWriter( new FileWriter(to) );
    io.Source.fromFile(from).getLines.foreach(s => out.write(s + "\n"));
    out.close()
  }

}
