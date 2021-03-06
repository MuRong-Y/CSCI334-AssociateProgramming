# CSCI334-AssociateProgramming
### Author: Rongxin Yang

### Library Source: 
File, FileNotFoundException, Scanner, Vector

### Declaration:
All work are done by myself, no copy. And the calculation is based on the formula that given by my professor.

## Problem Solving:
This program is that in a given varaibles and a given orders (in this program has only 2 or 3 orders), determine the a given variable pattern with or possible values are statistically significant association pattern or not. The purpose of this is to find the associate between several items.

## Design and Explanation of Code (including How to use this program):
First, it will read the input and return a table we need. Then the program will ask users which order they want, in this case, it only can be 2 or 3. Then it asks users to enter the column they want to view, but the selected number should be in the parentheses of the question. For 2nd order, need to answer 2 times for 2 columns; for 3rd order, need to answer 3 times for 3 columns. After that, the program will calculate the association between these columns and display whether they are in association mode.

There are 2 main steps to calculate:
1. Support Measure: include methods SupportMeasure3 and SupportMeasure2
2. Level of dependency in terms of mutual information measure: include methods LevelDependency3 and LevelDependency2

Other main methods:
- Methods Associate3 and Associate2 work for all possible value with their specified columns.
- Methods IntersectProb3 and Count3 work for calculating Intersection Probability; similar with IntersectProb2 and Count2.
- Methods RowMatch3, RowMatch2, RowMatch1 work for finding the specified pattern in which rows and how many rows match and then return as Vector.


## Explanation of input file:
In the input file, 
1. first line is threshold, 
2. second line is row and column number, 
3. third line is number of states for each variable. 
4. The remaining rows are a table
5. Each rows' last number is frequency or count

## Limitation:
It can only calculate 2nd order and 3rd order that can be decided by users with combination of all possible values of specific columns that are entered by users (NOT permute for all possibles, only for the columns that user enter). 
It cannot deal with general problems with 4th or more orders. 
The functions in the program end by 2 is work for 2nd Order, and end by 3 is work for 3ed Order. 
The users must enter the correct numbers in the question, otherwise it will report an error or end the operation.

