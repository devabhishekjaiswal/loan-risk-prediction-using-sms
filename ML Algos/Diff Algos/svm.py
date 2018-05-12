print(__doc__)

from sklearn.metrics import accuracy_score
import numpy as np
import pandas as pd
from sklearn import metrics
import sys
import matplotlib.pyplot as plt
from sklearn import svm, datasets

# import some data to play with


iris = datasets.load_iris()
print iris
X = np.append(iris.data[10:90, :2],  iris.data[100:140, :2])    # we only take the first two features. We could
                                                                       # avoid this ugly slicing by using a two-dim dataset
print np.where(iris.target==2)

X = np.reshape(X, (-1, 2))

y = np.append(iris.target[10:90], iris.target[100:140])


X_test = np.append(iris.data[:10, :2],  iris.data[90:100, :2])
X_test = np.append(X_test, iris.data[140:, :2])
X_test = np.reshape(X_test, (-1, 2))
Y_test = np.append(iris.target[:10], iris.target[90:100])
Y_test = np.append(Y_test, iris.target[140:])

y_true = y
print iris.target

h = .02  # step size in the mesh

C = 1.0  # SVM regularization parameter
svc = svm.SVC(kernel='linear', C=C).fit(X, y)
rbf_svc = svm.SVC(kernel='rbf', gamma=0.7, C=C).fit(X, y)
poly_svc = svm.SVC(kernel='poly', degree=3, C=C).fit(X, y)
lin_svc = svm.LinearSVC(C=C).fit(X, y)



print Y_test

for i, clf in enumerate((svc, lin_svc, rbf_svc, poly_svc)):

     Z = clf.predict(X_test)
     accuracy = metrics.accuracy_score(Y_test, Z)
     print(accuracy)
     print Z
