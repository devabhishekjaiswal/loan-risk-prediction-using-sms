import pandas as pd
import numpy as np
from sklearn.feature_extraction import DictVectorizer
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn import metrics
from sklearn import svm, datasets
from sklearn import cross_validation
# iris = load_iris()

df = pd.read_json('Reduced.json', orient='list')

target = []

for label in df['label']:
    if label == 'NO':
        target.append(0)
    else:
        target.append(1)

print len(target)
dfdata = df[['credited', 'debited', 'rechargeAmount']]

# dfdata = df[['credited', 'debited']]
# # print dfdata
# target_Train = target[:200]
Train = dfdata[:200]

# print target_Train

h = 50  # step size in the mesh

C = 1  # SVM regularization parameter

# svc = svm.SVC(kernel='linear', C=C).fit(Train, target_Train)

rbf_svc = svm.SVC(kernel='rbf', gamma=0.5, C=C)
# print 'here'
# # poly_svc = svm.SVC(kernel='poly', degree=4 , C=C).fit(Train, target_Train)
# # lin_svc = svm.LinearSVC(C=C).fit(Train, target_Train)
# print 'here'
# test = dfdata[500:]
# print test
# for i, clf in enumerate((svc, lin_svc, rbf_svc, poly_svc)):


clf = rbf_svc
test = dfdata[200:]
target = target[200:]
print "SVM accuracy taking k = 6 using Cross Validation Accuracy Measure "
print cross_validation.cross_val_score(clf, test, target, cv=6)


# clf.fit(Train, target[:200])
# # print i
# Z = clf.predict(test)
# print Z
# # print target[200:]
# accuracy = metrics.accuracy_score(target, Z)
# print accuracy