import pandas as pd
import numpy as np
from sklearn.feature_extraction import DictVectorizer
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn import metrics
from sklearn import svm, datasets

# iris = load_iris()

df = pd.read_json('my.json', orient='list')

target = []

for label in df['label']:
    if label == 'NO':
        target.append(0)
    else:
        target.append(1)

print len(target)

dfdata = df[['credited', 'debited']]
# print dfdata
target_Train = target[:500]
Train = dfdata[:500]

# print target_Train

h = 50  # step size in the mesh

C = 1  # SVM regularization parameter

# svc = svm.SVC(kernel='linear', C=C).fit(Train, target_Train)

rbf_svc = svm.SVC(kernel='rbf', gamma=0.5, C=C).fit(Train, target_Train)
print 'here'
# poly_svc = svm.SVC(kernel='poly', degree=4 , C=C).fit(Train, target_Train)
# lin_svc = svm.LinearSVC(C=C).fit(Train, target_Train)
print 'here'
test = dfdata[500:]
# print test
# for i, clf in enumerate((svc, lin_svc, rbf_svc, poly_svc)):
clf = rbf_svc

# print i
Z = clf.predict(test)
print Z
print target[500:]
accuracy = metrics.accuracy_score(target[500:], Z)
print accuracy