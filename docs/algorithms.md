# Algorithm Notes

**Cost function f(s):** The lower bound of a (partial) Schedule's complete length. 

E.g If f(S1) = 8 and S1 is a partial schedule, then we know any complete schedule stemming from S1 is at least 8 units long.

**Ways of calculating cost function**:

1. **f(s)** = (Sum of task weights + current idle time of schedule) / number of processors. This essentially takes the current schedule length (including the wasted processing time) and assumes the rest of the tasks will be spread perfectly across the processors.
2. **f(s)** = maximum of: (start time of each scheduled task + its bottom level). 
   **Note:** Bottom level of a task is the longest path to any exit node i.e. the shortest possible time that the process can finish from the point of this task's execution (because of the dependencies that must occur sequentially).

**DFS Approach (pseudo-code):**

~~~
best = infinity  // best schedule so far
s = empty stack // keeps track of schedules we need to expand
initialState = empty schedule
s.push(initialState)
while S is not empty
	V = s.pop() // partial schedule that we build upon at any step
	Mark V as visited
	If f(v) > best:
		Get rid of whole subtree starting with v because schedules stemmed from v will always be non-optimal
	If V is a complete schedule:
		If v.schedule_length < Best
		Best = v.schedule_length
	Else for all possible processor-task assignments, a:
		Build schedule S' by extending V by assignment a
		If S' has not already been considered (not marked visited)
			Calculate f(S')
			S.push(S')
Return Best and schedule that caused the value of Best
~~~

**A\* Approach:**

~~~~
maintain priority-queue OPEN, sorted by f() values in increasing order.
OPEN.add(empty state)

while !OPEN.isEmpty():
	s = OPEN.pop()
	if s is a complete schedule, return s
	
	build all possible schedules stemming from s, calculate f() for each and add each to OPEN.

// if we get this far something is wrong because the optimal solution should be found before we have exhausted the search space.
~~~~

**Greedy Approach (non-optimal but good)**:

~~~
freeNodes = taskGraph.getEntryNodes();

// continue until all nodes are scheduled
while !freeNodes.isEmpty():
	for each node in freeNodes:
		calculate earliest possible start time across all processors
		
	take node T with earliest possible start time and allocate it to the processor allowing 	this start time.
	
	// remove scheduled node from list of free nodes
	freeNodes.remove(T)

	// update list of free nodes
	for each unscheduled node N:
		if each of N.dependsOn() are scheduled:
			freeNodes.add(N)
			
return schedule
~~~