Course -> Distributed Computing 6380
                                                                                               
Purpose -> Group Project 2                                      
                                                                                          
No of Java Files -> 7                                            
                                                                                           
   Group Members: 1. Maxwell Hall                           
                  2. Prashant Prakash                       
                  3. Shahshank Adidamu                     
                                                                                                  
#################################################

Note : For running this Program "JAVA8" must be installed in the system.

Steps to Run the program 
1.  unzip the folder containing code 
	unzip asynchbfs.zip
2. Change to directory asynchbfs
cd asynchbfs 
3. Compile all java Files 
javac *.java
// running Main.java
4. Run the class having main function (Main)

Usage of this class:
java Main p1 
p1: Path of file containing information about process and weights 

Sample Commands: 

java Main /home/004/p/px/pxp141730/connectivity.txt

NOTE: The output will also include messages marking the completion of each round.

Sample Input: 

12, 7
1 1 0 1 0 0 0 1 0 0 0 0
1 1 1 0 1 0 0 0 0 0 0 0
0 1 1 0 1 1 0 0 0 0 0 0
1 0 0 1 0 0 1 1 0 0 0 0
0 1 1 0 1 1 0 1 1 0 0 0
0 0 1 0 1 1 0 0 0 0 0 1
0 0 0 1 0 0 1 1 0 1 0 0
1 0 0 1 1 0 1 1 0 0 1 0
0 0 0 0 1 0 0 0 1 0 1 1
0 0 0 0 0 0 1 0 0 1 1 1
0 0 0 0 0 0 0 1 1 1 1 1
0 0 0 0 0 1 0 0 1 1 1 1

output:

The root is process 7
Process Parent Distance
1       8      2       
2       1      3       
3       5      3       
4       7      1       
5       8      2       
6       12     3       
7       -1     0       
8       7      1       
9       12     3       
10      7      1       
11      10     2       
12      10     2       

Adjacency List:
NODE 11  adjacency list: 10 
NODE 1   adjacency list: 2 8 
NODE 12  adjacency list: 6 9 10 
NODE 2   adjacency list: 1 
NODE 3   adjacency list: 5 
NODE 4   adjacency list: 7 
NODE 5   adjacency list: 3 8 
NODE 6   adjacency list: 12 
NODE 7   adjacency list: 4 8 10 
NODE 8   adjacency list: 1 5 7 
NODE 9   adjacency list: 12 
NODE 10  adjacency list: 11 12 7 