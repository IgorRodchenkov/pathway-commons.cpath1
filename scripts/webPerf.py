import urllib
import timeit

baseUrl = "http://toro.cbio.mskcc.org:8080/cpath/"
#baseUrl = "http://localhost:8080/cpath/"
#baseUrl = "http://cbio.mskcc.org/cpath/"

# Hit the home page to init the web app
def hitHomePage():
    print "Hitting home page to init web app..."
    url = baseUrl + "home.do?debug=1"
    f = urllib.urlopen(url)
    s = f.read()
    f.close

# Hit the Web API;  do not check XML cache
def hitWebAPI(useOptimizedCode):
    print "Hit Web API: ",
    url = baseUrl + "webservice.do?version=1.0&cmd=get_by_keyword&q=protein&format=psi_mi&maxHits=20&checkXmlCache=0"
    if (useOptimizedCode):
	url += "&useOptmizedCode=1"
	print "execute new code"
    else:
	url += "&useOptimizedCode=0"
	print "execute old, crappy code"
    f = urllib.urlopen(url)
    s = f.read()
    #print s
    f.close

def outputTrials (trials):
    print "Times for individual trials shown below;  all values shown in ms"
    totalTime = 0.0
    for trial in trials:
	print "%0.2f" % (1000.0 * trial)
	totalTime += trial
	average = totalTime / numTrials
    print "Average: %0.2f ms" % (1000.0 * average)

hitHomePage()
numTrials = 25
print "Test performance of web API (execute old, crappy code).  Total # of trials:  ", numTrials
t = timeit.Timer("hitWebAPI(0)", "from __main__ import hitWebAPI")
trials = t.repeat(numTrials, 1)
outputTrials(trials)

print "Test performance of web API (execute new code).  Total # of trials:  ", numTrials
t = timeit.Timer("hitWebAPI(1)", "from __main__ import hitWebAPI")
trials = t.repeat(numTrials, 1)
outputTrials(trials)
