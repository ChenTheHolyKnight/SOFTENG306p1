# SoftEng306Project1
The wiki contains meeting agenda/minutes and algorithm research and implementations
Our project plan can be found in the "docs" folder
## Project Description
This project is about using artificial intelligence and parallel processing power to solve a difficult
scheduling problem. It is about Speed! In this project of the SoftEng306 design course your client is
the leader of a Parallel Computing Centre, who needs you to solve a difficult scheduling problem for
his parallel computer systems. This client happens to be Oliver Sinnen. The project is well defined
and scoped, but the particular challenge of this project are the (possibly) contradicting objectives:
developing a solution with a Software Engineering approach and design while having a very fast
execution speed. At the end of the project your solutions will compete against each other in a speed
comparison. Your team might choose to contribute the solution to the PARC lab for their work.

## Members
| Contributors      | UPI         | Github Link                             |
| ----------------- | ----------- | --------------------------------------- |
| Ravid Aharon      | Raha837     | https://github.com/Ravid12              |
| Sam Broadhead     | Sbro348     | https://github.com/swimuel              |
| Darcy Cox         | Dcox740     | https://github.com/darcycox97           |
| Victoria Skeggs   | Vske511     | https://github.com/victoriaskeggs       |
| Chen Zhao         | Czha959     | https://github.com/ChenTheHolyKnight    |

## Project Setup
This project uses Gradle. In order to build the project, navigate to the root folder of this repository 
in the command line and type the command
`./gradlew build`
This will run the tests and build the executable jar file. (note: due to this project's use of the GraphStream
library, the build can take a couple minutes to package dependencies into the jar)

## CLI Usage
```
usage: <INPUT GRAPH FILENAME> <NUMBER OF PROCESSORS> [OPTIONS]
 OPTIONS:
 -a <arg>   the algorithm implementation to use for scheduling (default is
            dfs) (if number of Cores [-p > 1], algorithm can only use dfs).
            Acceptable Values: dfs | astar | greedy | simple
 -f <arg>   comma-separated list of cost functions to be used (default is
            bl, it and drt)
            Acceptable values: bl | it | drt
 -o <arg>   name of output file (default is <INPUT>-output.dot)
 -p <arg>   number of cores to execute program on (default is 1 core)
 -P <arg>   comma-separated list of pruners to be used (default is both es
            and ne)
            Acceptable values: es | ne
 -v         visualise the search
```


    
