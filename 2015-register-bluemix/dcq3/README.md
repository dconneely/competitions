# Question 3: Simultaneous equations

Given a pair of simultaneous equations, it is possible to deduce the values of the variables contained therein.
Imagine, for instance, we have the following pair of simultaneous equations:

* 4x+3y=24 (call this equation [1])
* 5x+y=19 (call this equation [2])

Using simple GCSE-level mathematics, we can calculate the value of x thus:

* Multiply [2] by 3 throughout: 5x+y=19 becomes 15x+3y=57
* Subtract 15x from both sides: 15x+3y=57 becomes 3y=57-15x
* Subtract 4x from both sides of [1]: 4x+3y=24 becomes 3y=24-4x
* Because 3y=57-15x (from [2]) and 3y=24-4x (from [1]) we can say 57-15x = 24-4x
* Subtract 24 from both sides: 33-15x = -4x
* Now add 15x to both sides: 33=11x
* Finally, divide both sides by 11: 3=x (or x=3).
* Now we know that x=3, we can substitute its value into one of the equations. 4x+3y=24 [1] becomes 4x3+3y=24, or 12+3y=24
* Subtract 12 from both sides: 3y=12
* Divide both sides by 3: y=4
* We have therefore deduced that x=3 and y=4.

Write a program that takes a series of pairs of simultaneous equations, and computes x and y in each case. Each
equation takes the form:

* NxSMy=R

... where:

* N and M are either blank or an integer between 1 and 100 inclusive
* S is a sign, either + or -
* R is an integer between -5000 and 5000 inclusive

Each equation in a pair will appear on a separate line, and will always contain some multiple of x and y. Each pair
will be followed by a single # mark on a line by itself. The end of the input stream will be denoted by the '##' mark
on a line by itself. The elements of any equation will be separated by zero or more elements of whitespace (either
spaces or tab characters).

Your program should write a solution for each pair of equations, each on a line by itself and each of the form:

* x=P and y=Q

... where P and Q are integers between -40 and 40 inclusive. You may assume that every equation set is solvable, and
that there is only one possible value for each of x and y for any pair of equations.

## Sample Input

* 4x+3y=24
* 5 x + y =19
* #
* 2x+y=3
* 3x-y=2
* #
* ##

## Sample Output

* x=3 y=4
* x=1 y=1
