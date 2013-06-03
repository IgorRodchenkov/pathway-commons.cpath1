import urllib
import timeit

#baseUrl = "http://toro.cbio.mskcc.org:8080/cpath/"
#baseUrl = "http://localhost:8080/cpath/"
baseUrl = "http://cbio.mskcc.org/cpath/"

# Hit the home page to init the web app
def hitHomePage():
    print "Hitting home page to init web app..."
    url = baseUrl + "home.do?debug=1"
    f = urllib.urlopen(url)
    s = f.read()
    f.close

# Hit the Web API;  do not check XML cache
def batchDownload (useOptimizedCode, numInteractions, maxHits):
    print "Settings:"
    print "-->  Number of interactions to retrieve:  ", numInteractions
    print "-->  Number of interactions to retrieve in each request:  ", maxHits
    maxIterations = int(numInteractions / maxHits)
    for i in range (0,maxIterations):
	startIndex = i * maxHits
	url = baseUrl + "webservice.do?version=1.0&cmd=get_by_keyword&q=dna&format=psi_mi&maxHits=" 
	url += str(maxHits) + "&checkXmlCache=0"
	if (useOptimizedCode):
	    url += "&useOptmizedCode=1"
	else:
	    url += "&useOptimizedCode=0"
	url += "&startIndex=" + str(startIndex)
	print "Getting interactions:  [%d, %d]" % (startIndex, (startIndex+maxHits))
	#print "Url:  ", url
	f = urllib.urlopen(url)
	s = f.read()
	f.close

hitHomePage()
numInteractions = 1000
maxHits = 50
numTrials = 1
print "Test performance of web API (execute old, crappy code)."
t = timeit.Timer("batchDownload(0," + str(numInteractions) + "," + str(maxHits) +")", "from __main__ import batchDownload")
oldCodetrials = t.repeat(numTrials, 1)
timeOldCode = 0.0
for trial in oldCodetrials:
    print "Total time:  %0.2f ms" % (1000.0 * trial)
    timeOldCode += trial
throughputOldCode = numInteractions / timeOldCode
print "Throughput:  %2.4f interactions / sec" % throughputOldCode 

print "\nTest performance of web API (execute new code). "
maxHits = 500
timeNewCode = 0.0
t = timeit.Timer("batchDownload(1,"+ str(numInteractions) + "," + str(maxHits) +")", "from __main__ import batchDownload")
newCodetrials = t.repeat(numTrials, 1)
for trial in newCodetrials:
    print "Total time:  %0.2f ms" % (1000.0 * trial)
    timeNewCode += trial
throughputNewCode = numInteractions / timeNewCode
print "Throughput:  %2.4f interactions / sec" % throughputNewCode

performanceDelta = timeOldCode / timeNewCode
throughputDelta = throughputNewCode / float(throughputOldCode)

print "\nPerformance of old code:  %0.4f ms" % timeOldCode
print "Performance of new code:  %0.4f ms" % timeNewCode
print "Throughput of old code:  %2.4f interactions / sec" % throughputOldCode
print "Throughput of new code:  %2.4f interactions / sec" % throughputNewCode 
print "\nPerformance gain is:  %2.8f" % performanceDelta
print "Throughput gain is: %2.8f" % throughputDelta

print "Done."
