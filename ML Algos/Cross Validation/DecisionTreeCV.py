import pandas as pd
import numpy as np
from sklearn.feature_extraction import DictVectorizer
from sklearn import tree
from sklearn import metrics
from sklearn import cross_validation
import matplotlib.pyplot as plt
from inspect import getmembers
from mpl_toolkits.mplot3d import Axes3D

df = pd.read_json('Reduced.json', orient='list')

print df[['credited', 'debited', 'rechargeAmount', 'label']]

# print(X)

target = []


for label in df['label']:
    if label == 'NO':
        target.append(0)
    else:
        target.append(1)

print "Decision Tree accuracy using Cross Validation Accuracy Measure "
# print len(target)
# print df.columns
dfdata = df[['credited', 'debited', 'rechargeAmount']]


# ------------------------------------------------------------
# plt.plot(df['credited'], df['debited'], 'ro')
# plt.show()
# print X
# -----------------------------------------------------------------


# _______Training our data___________________

clf = tree.DecisionTreeClassifier()
clf.fit(dfdata[:250], target[:250])
print ""
print cross_validation.cross_val_score(clf, dfdata, target, cv=5)


clf.fit(dfdata, target)
tree.export_graphviz(clf, out_file='tree2.dot')
# print clf.tree_.feature
# print clf.tree_.feature
# _________________________________________
#
test = dfdata[250:]

# test = np.array([541.0, 4545.0, 4054.0]).reshape(-1, 3)
print clf.predict(test)
# print target[450:]
accuracy = metrics.accuracy_score(clf.predict(test), target[250:])
# print metrics.confusion_matrix(clf.predict(test), target[450:])
#
#
print accuracy