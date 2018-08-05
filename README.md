# SoftEng306Project1

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
| Sam Broadhead     | Sbro     | https://github.com/swimuel              |
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
Usage:  java -jar Scheduler.jar [path-to-DOT-file] [Number-of-processors] [-options]

where options include:
    -p <numCores>       Choose the number of computer cores to run the algorithm on,
                        where <numCores> is the number of cores. 
                        (default is 1)
  
    -v                  Shows a visualization of the algorithm while it is running.
                        (may slow down performance)
  
    -o <name>           Sets output name of the output DOT file, where <name> is the 
                        name of the output file.
                        (default is "[path-to-DOT-file]-output.dot")
```


    
