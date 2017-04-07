"""
@author: Wei Wei
wei0496@bu.edu
BUID:U85731466
"""

import numpy as np
import matplotlib.pyplot as plt

class NeuralNet:
    """
    This class implements a simple 3 layer neural network.
    """
    
    def __init__(self, input_dim, output_dim, epsilon):
        """
        Initializes the parameters of the neural network to random values
        """
        
        self.W1 = np.random.randn(input_dim, 3) / np.sqrt(input_dim)
        self.b1 = np.zeros((1, 3))
        self.W2 = np.random.randn(3, output_dim) / np.sqrt(3)
        self.b2 = np.zeros((1, output_dim))
        self.epsilon = epsilon
        
    #--------------------------------------------------------------------------
    
    def compute_cost(self,X, y):
        """
        Computes the total loss on the dataset
        """
        
        num_samples = len(X)
        # Do Forward propagation to calculate our predictions
        z1 = X.dot(self.W1) + self.b1
        a1 = np.tanh(z1)
        z2 = a1.dot(self.W2) + self.b2
        exp_z = np.exp(z2)
        softmax_scores = exp_z / np.sum(exp_z, axis=1, keepdims=True)
        # Calculate the cross-entropy loss
        cross_ent_err = -np.log(softmax_scores[range(num_samples), y])
        data_loss = np.sum(cross_ent_err)
        return 1./num_samples * data_loss

    
    #--------------------------------------------------------------------------
 
    def predict(self,x):
        """
        Makes a prediction based on current model parameters
        """
        # Do Forward Propagation
        z1 = x.dot(self.W1) + self.b1
        a1 = np.tanh(z1)
        z2 = a1.dot(self.W2) + self.b2
        exp_z = np.exp(z2)
        softmax_scores = exp_z / np.sum(exp_z, axis=1, keepdims=True)
        return np.argmax(softmax_scores, axis=1)
        
    #--------------------------------------------------------------------------
    
    def fit(self,X,y,num_epochs):
        """
        Learns model parameters to fit the data
        """
        for i in range(0,num_epochs):
            # Do Forward propagation to calculate our predictions
            z1 = X.dot(self.W1) + self.b1
            a1 = np.tanh(z1)
            z2 = a1.dot(self.W2) + self.b2
            exp_z = np.exp(z2)
            softmax_scores = exp_z / np.sum(exp_z, axis=1, keepdims=True)
            
            # Back Propagation
            delta3 = softmax_scores
            delta3[range(len(X)),y] -= 1
            dw2 = (a1.T).dot(delta3)
            db2 = np.sum(delta3, axis = 0, keepdims = True)
            delta2 = delta3.dot(self.W2.T) * (1 - np.power(a1, 2))
            dw1 = np.dot(X.T, delta2)
            db1 = np.sum(delta2, axis = 0)
            
            # Gradient descent parameter uqdate
            self.W1 -= self.epsilon * dw1
            self.b1 -= self.epsilon * db1
            self.W2 -= self.epsilon * dw2
            self.b2 -= self.epsilon * db2
            
        return 0
            
            
        ###TODO:
        #For each epoch
        #   Do Forward Propagation
        #   Do Back Propagation
        #   Update model parameters using gradients

#--------------------------------------------------------------------------
#--------------------------------------------------------------------------

def plot_decision_boundary(pred_func):
    """
    Helper function to print the decision boundary given by model
    """
    # Set min and max values
    x_min, x_max = X[:, 0].min() - .5, X[:, 0].max() + .5
    y_min, y_max = X[:, 1].min() - .5, X[:, 1].max() + .5
    h = 0.01
    # Generate a grid of points
    xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
    # Predict the function value for the whole gid
    Z = pred_func(np.c_[xx.ravel(), yy.ravel()])
    Z = Z.reshape(xx.shape)
    # Plot the contour and training examples
    plt.contourf(xx, yy, Z, cmap=plt.cm.Spectral)
    plt.scatter(X[:, 0], X[:, 1], c=y, cmap=plt.cm.Spectral)
    plt.show()

#--------------------------------------------------------------------------
#--------------------------------------------------------------------------

#Train Neural Network on
linear = False

#A. linearly separable data

if linear:
    #load data
    X = np.genfromtxt('/Users/m804/Desktop/CS440/PA2/three layer neural net/DATA/ToyLinearX.csv', delimiter=',')
    y = np.genfromtxt('/Users/m804/Desktop/CS440/PA2/three layer neural net/DATA/ToyLineary.csv', delimiter=',')
    y = y.astype(int)
    #plot data
    #plt.scatter(X[:,0], X[:,1], s=40, c=y, cmap=plt.cm.Spectral)
    #plt.show()
#B. Non-linearly separable data

else:
    #load data
    X = np.genfromtxt('/Users/m804/Desktop/CS440/PA2/three layer neural net/DATA/ToyMoonX.csv', delimiter=',')
    y = np.genfromtxt('/Users/m804/Desktop/CS440/PA2/three layer neural net/DATA//ToyMoony.csv', delimiter=',')
    y = y.astype(int)
    #plot data
    #plt.scatter(X[:,0], X[:,1], s=40, c=y, cmap=plt.cm.Spectral)
    #plt.show()

input_dim = 2 # input layer dimensionality
output_dim = 2 # output layer dimensionality

# Gradient descent parameters 
epsilon = 0.025 
num_epochs = 5000

def helpplot(X, y):
    y1 = [0] * 20
    for i in range(0,20):
        num_epochs = i * 20
        NN.fit(X, y, num_epochs)
        y1[i] = NN.compute_cost(X,y)
    return y1

#Fit model
#----------------------------------------------
#Uncomment following lines after implementing NeuralNet
#----------------------------------------------
x1 = [0.01,0.025,0.07]
x2 = [x * 20 for x in range(20)]
epsilon = x1[0]
NN = NeuralNet(input_dim, output_dim, epsilon)
y1 = helpplot(X,y)
epsilon = x1[1]
NN = NeuralNet(input_dim, output_dim, epsilon)
y2 = helpplot(X,y)
epsilon = x1[2]
NN = NeuralNet(input_dim, output_dim, epsilon)
y3 = helpplot(X,y)
plt.axis([0, 400, 0, 20])
plt.plot(x2,y1,'r--',x2,y2,'b--',x2,y3,'g--')
plt.title("different learning rate")
plt.show()



    #
    # Plot the decision boundary
    # plot_decision_boundary(lambda x: NN.predict(x))
    # plt.title("Neural Net Decision Boundary")
            
    