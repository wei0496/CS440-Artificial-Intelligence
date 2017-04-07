'''
CS440 Programming Assignment 2
statepath.py
    Determine the optimal state path for each observation set, report its probability
    Viterbi algorithm is implemented

Wei Wei wei0496@bu.edu
Teammates:
    Tianqi Xu
    Yehui Huang
    Zhou Xiao

2016/4/4
'''

import sys

#Assumed input parameters from sys.argv would be:
#'statepath.py'
#'sentence.hmm'
#'example[x].obs'

#Error checking
if len(sys.argv) != 3:
    print 'Invalid inputs!'
    sys.exit()

#Viterbi Algorithm to find the path and the prob
def viterbi(obs, states, Pi, A, B, observations):
    V = [{}]
    path = {}
    
    # Initialize base cases (t == 0)
    for index, elem in enumerate(states):
        V[0][elem] = Pi[0][index] * B[index][observations.index(obs[0])]
        path[elem] = [elem]
    
    # Recursion
    for t in range(1,len(obs)):
        V.append({})
        newpath = {}
        
        for index, elem in enumerate(states):
            (prob, state) = max([(V[t-1][elem0] * A[index0][index], index0) for index0, elem0 in enumerate(states)])
            V[t][elem] = prob * B[index][observations.index(obs[t])]
            
            newpath[elem] = path[states[state]] + [elem]
    
        # Don't need to remember the old paths
        path = newpath
    
    #Ternimation
    (prob, state) = max([(V[len(obs) - 1][elem], elem) for elem in states])
    return (prob, path[state])

sentence = open(sys.argv[1], 'r')
lines = sentence.readlines()
idx = 0

# Prepare the variables
# take N,M,T as input
file2 = lines[idx]
N = int(file2[0])
M = int(file2[2])
T = int(file2[4])

idx += 1
# read the states
state = lines[idx]
states = state.split()

idx += 1
# read the observations
observation = lines[idx]
observations = observation.split()

idx += 1
# read the matrix a,matrix b and pi
A = []
B = []
Pi = []

if lines[idx][0] == 'a':
    idx += 1
    for i in range(0,N):
        a = lines[idx].split()
        A.append(map(float, a))
        idx += 1

if lines[idx][0] == 'b':
    idx += 1
    for i in range(0,N):
        b = lines[idx].split()
        B.append(map(float, b))
        idx += 1

if lines[idx][0] == 'p':
    idx += 1
    pi = lines[idx].split()
    Pi.append(map(float, pi))
    idx += 1

'''
print("observations")
print(observations)
print("states")
print(states)
print("start_probability")
print(Pi)
print("transition_probability")
print(A)
print("emission_probability")
print(B)
'''

#collect info from example[x]
example = open(sys.argv[2], 'r')
obs = example.readlines()
idx = 0
num_datasets = int(obs[idx])

#Start forward procedure
idx += 1
for i in range(0,num_datasets):
    num_o = obs[idx]
    idx += 1
    o = obs[idx].split()
    idx += 1
    prob, path = viterbi(o, states, Pi, A, B, observations)
    
    if(prob == 0):
        print prob
    else:
        print ('%s ' %prob + ' '.join(path))