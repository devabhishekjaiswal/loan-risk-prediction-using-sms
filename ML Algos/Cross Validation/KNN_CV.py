import pandas as pd
from sklearn import cross_validation
from sklearn import metrics
from sklearn.neighbors import KNeighborsClassifier

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
# print dfdata



# Hold up

# target_Train = target[:450]
# Train = dfdata[:450]
#

# rbf_svc = svm.SVC(kernel='rbf', gamma=0.5, C=C).fit(Train, target_Train)
#
print 'here'
test = dfdata[230:]


neigh = KNeighborsClassifier(n_neighbors=7)
print "KNN accuracy Neighbours = 7 taking k = 4 using Cross Validation Accuracy Measure "
print cross_validation.cross_val_score(neigh, dfdata, target, cv=4)

# # print test
# # for i, clf in enumerate((svc, lin_svc, rbf_svc, poly_svc)):
# clf = rbf_svc
#
# # print i
neigh.fit(dfdata[:230], target[:230])
weight = []
for x in target[230:]:
    if x == 1:
        weight.append(2)
    else:
        weight.append(1)
#
Z = neigh.predict(test)
# print Z[:]
# print target[230:]
accuracy = metrics.accuracy_score(target[230:], Z)
# print accuracy
# print neigh.score(test, target[230:], weight)
