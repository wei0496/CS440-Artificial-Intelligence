'''
CS440 Programming Assignment 2
recognize.py
    The forward part of the forward/backward procedure
    
References:
    A Tutorial on Hidden Markov Models and Selected Applications in Speech Recognition by Rabiner

Wei Wei wei0496@bu.edu
Teammates:
    Tianqi Xu
    Yehui Huang
    Xiao Zhou
    

2016/4/3
'''

import sys

#Assumed input parameters from sys.argv would be:
#'recognize.py'
#'sentence.hmm'
#'example[x].obs'

#Error checking
if len(sys.argv) != 3:
    print 'Invalid inputs!'
    sys.exit()
    
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
#A = np.array(A)

if lines[idx][0] == 'b':
    idx += 1
    for i in range(0,N):
        b = lines[idx].split()
        B.append(map(float, b))
        idx += 1
#B = np.array(B)

#Note that the end character of pi might be different in different system (Unix, windows)
#Therefore, the first character might be the most stable way of checking
if lines[idx][0] == 'p':
    idx += 1
    pi = lines[idx].split()
    Pi.append(map(float, pi))
    idx += 1
#Pi = np.array(Pi)

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
    #print o

    # initization
    for x, y in enumerate(o):
        f_curr = []
        state_index = observations.index(y)
        for j in range(0,len(states)):
            if x == 0:
                #initialization
                f_curr.append(Pi[0][j] * B[j][state_index])
            else:
                #induction
                s = sum(f_prev[z] * A[z][j] for z in range(0,len(states)))
                alpha = B[j][state_index] * s
                f_curr.append(alpha)
        f_prev = f_curr
    print(sum(f_curr))
