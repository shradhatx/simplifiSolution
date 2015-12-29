# simplifiSolution
Solution for simplify technical challenge provided by Shradha Bhalla
1. Problem:
The given data file (or the log file) has the following useful information : IP, UA, U, R
One record spans multiple lines.

Objective1: parse the records and extract all relevant information (particulrly keywords relevant for decision making for bidding for ad),  for analysis, storage  and quick lookup of information as stated in objective 2 and 3 below.
Objective2: given an ip get the complete information about the record
Objective3: given a keyword : get all ips that had the  keyword

2. Assumption: keywords file is available with relevant keywords (generated after objective 1) - 1 keyword(or String) in each line.
To generate this file process a test data file using the same steps as below. Review the keyw.txt that is generated and rename it as keywords.txt
This keyword list can be further improved applying machine learning concepts.

3. Solution description: Program is written in scala. This is to keep in mind the distributed in memory processing by easy extension of the program to Spark(use of scala).
The intermediate data(from in memory maps)  is right now written to files (hdfs file system is recommended). With spark an in memory cache will be used.

Solution to Objective1:  the below command creates 4 files(only 3 relevant for meeting the objectives) :
     i) keyw.txt : keywords file
     ii) ipfullinfo.txt : ip to full record info
     iii) kwipmap.txt : keyword to ip map
     iv)  ipmap.txt : records in csv file format(^ as field separator) to build dataframe in spark

To run:
      scala SbSolution <datafle to process> <keywords file>

Solution to Objective2:  shell command below retrieves desired information
      grep <ipaddress> ipfullinfo.txt

Solution to Objective3:  shell command below retrieves desired information
      grep <keyword> kwipmap.txt

4. Recommendations:
i) It is definitely not recommended to process the files on a single machine.
For performance reason a distributed processing environment spark is recommended for in memory Maps and distributed file system hdfs or distributed data store like Hbase or Cassandra
ii) keywords file provided as command argument can be created initially by running  test data file and gradually built on by machine learning algorithms on subsequent file processing and applying feedback loop.
iii) keywords file keyw.txt that is generated does not fully encompass all relevant keywords there are. But it can be easily extended such as - parsing utm_source or clicks etc
