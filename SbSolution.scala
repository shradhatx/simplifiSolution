/***********
The data file has the following information : IP, UA, U, R and one record spans multiple lines.
Objective1: parse the data to txt files -  extract all relevant keywords for analysis; for lookup
Objective2: given an ip: get complete information about the IP
Objective3: given a keyword : get all ips with keyword

Assumption: keywords file is available with relevant keywords (generated after objective 1) - 1 keyword(or String) in each line.

Solution to Objective1:  the below command creates 3 files : i)keywords file: keyw.txt ii) ip to info map: ipmap.txt iii) keyword to ip map: kwipmap.txt
      scala SbSolution <datafle to process> <keywords file>
Solution to Objective2:  shell command below retrieves desired information
      grep <ipaddress> ipmap.txt
Solution to Objective3:  shell command below retrieves desired information
      grep <keyword> kwipmap.txt

Recommendations:
i) keywords file provided as command argument can be created initially by running  test data file and gradually built on by machine learning algorithms on subsequent file processing and applying feedback loop.
ii) keywords file keyw.txt that is generated does not fully encompass all relevant keywords there are. But it can be easily extended such as - parsing utm_source or clicks etc
iii) For better performance use a distributed processing environment - hadoop for distributed file storage and spark for in memory processing

*************/
import scala.io.Source
import java.io._
import java.io.{FileReader, FileNotFoundException, IOException}
import scala.collection.parallel.mutable.ParHashMap
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import Array._
object SbSolution {
   def main(args: Array[String]) {
      var i : Int = 0
      var jsonrec = "IP^UA^U^R"
      var fullrec = ""
      var datafile = ""
      var inputkwfile = ""
      val ipmap  = new PrintWriter(new File("ipmap.txt"))
      val ipfullinfo  = new PrintWriter(new File("ipfullinfo.txt"))
      val kwipfile  = new PrintWriter(new File("kwipmap.txt"))
      var keywordsSet: Set[String] = Set()
      //check for input args
      if(args.length > 0) {datafile = args(0)} else {println("No data file is provided. Using testdata.txt "); datafile = "testdata.txt"}
      println("using datafile: " + datafile)

      //check for inputkwfile, if one is provided as command line args use it or use keywords.txt as default
      if(args.length > 1) {inputkwfile = args(1)} else {println("No keywords file provided. Using keywords.txt as keywords file"); inputkwfile = "keywords.txt"}
      println("using keywordfile: " + inputkwfile)
      try {
        //for(line <- Source.fromFile("keywords.txt" ).getLines()){ keywordsSet=keywordsSet+(line) }
        for(line <- Source.fromFile(inputkwfile).getLines()){ keywordsSet=keywordsSet+(line) }
        } catch {
        case ex: IOException => println("Had an IOException trying to read keyword file: " + inputkwfile)
        case ex: FileNotFoundException => println("Couldn't find keyword file: " + inputkwfile)
        }
        //keywordsSet.foreach(println)


      val keywords  = new PrintWriter(new File("keyw.txt" ))

      //var kwip:Map[String,Set[String]] = mutable.Map()
      var kwip:Map[String,Set[String]] = Map()
      //var ips: mutable.Set[String] = Set()
      var ips: Set[String] = Set()
      var addip = ""
      ipmap.write(jsonrec+ "\n")
      jsonrec = ""

      try {
        for(line <- Source.fromFile(datafile).getLines()){
          var indexfrom = 0
          var indexto = 0
          var kword = ""
          if (line != "****") {
             fullrec = fullrec + line
             var f = line.split(": ").map(_.trim)
             if(f.length > 1){
                jsonrec = jsonrec + f(1)
                if(f(0) == "IP") { addip = f(1)}

                if(f(0) == "R") {
                                  //println(f(1))
                                  indexfrom = f(1).indexOfSlice("/",9);
                                  if(indexfrom > 0) {indexto = f(1).indexOfSlice("/",indexfrom+1);}
                                  if(indexto > 0) { kword = f(1).slice(indexfrom+1,indexto);
                                        keywords.write(kword); keywords.write("\n");
                                        //build the Map of keywords to set of ipaddresses  in memory for quick lookup of all ips given  a keyword
                                        if (keywordsSet.contains(kword)){
                                                if(kwip.contains(kword)){
                                                        ips = kwip(kword)
                                                        ips = ips+addip
                                                        kwip += (kword -> ips)
                                                        //for(x <- kwip(kword)) { println(x)}
                                                }else{
                                                        kwip += (kword -> Set(addip))
                                                        //for(x <- kwip(kword)) { println(x)}
                                                }
                                         }

                                        }
                                }
                }

             if(f(0) == "R") {fullrec = fullrec + "\n"; ipfullinfo.write(fullrec); fullrec = "";
                              jsonrec = jsonrec + "^"; ipmap.write(jsonrec+ "\n"); jsonrec = "";
                } else {jsonrec = jsonrec +  "^"; fullrec = fullrec + " "}

             }

          }

        //For now saving the Map of keywords to ipaddresses to a file
        for((k,v) <- kwip) {kwipfile.write(k +"-->" + v + '\n')}

        kwipfile.close()
        ipmap.close()
        keywords.close()
        println("Processing complete. Please read instrusctions on how to proceed further in ReadMe.Solution")
        println("Useful information in files : kwords.txt, ipmap, kwipmap.txt, ipfullinfo.txt. ")
        } catch {
        case ex: FileNotFoundException => println("Couldn't find data file.")
        case ex: IOException => println("Had an IOException trying to read data file")
        }

   }
}

