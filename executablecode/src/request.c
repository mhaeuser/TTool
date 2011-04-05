
#include <stdlib.h>
#include <unistd.h>

#include "request.h"
#include "mytimelib.h"
#include "myerrors.h"
#include "random.h"
#include "debug.h"


request *getNewRequest(int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params) {
  request *req = (request *)(malloc(sizeof(struct request)));
  
  if (req == NULL) {
    criticalError("Allocation of request failed");
  }

  makeNewRequest(req,  ID, type, hasDelay, minDelay, maxDelay, nbOfParams, params);  
  return req;
}


// Delays are in microseconds
void makeNewRequest(request *req, int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params) {
  long delay;

  req->next = NULL;
  req->listOfRequests = NULL;
  req->type = type;
  req->ID = ID;
  req->hasDelay = hasDelay;

  if (req->hasDelay > 0) {
    delay = computeLongRandom(minDelay, maxDelay);
    delayToTimeSpec(&(req->delay), delay);
  }

  req->selected = 0;
  req->nbOfParams = nbOfParams;
  req->params = params;

  req->alreadyPending = 0;
  req->delayElapsed = 0;

}




void destroyRequest(request *req) {
  free((void *)req);
}

int isRequestSelected(request *req) {
  return req->selected;
}

int nbOfRequests(setOfRequests *list) {
  int cpt = 0;
  request *req;

  req = list->head;

  while(req != NULL) {
    cpt ++;
    req = req->nextRequestInList;
  }

  return cpt;
}

request *getRequestAtIndex(setOfRequests *list, int index) {
  int cpt = 0;
  request * req = list->head;

  while(cpt < index) {
    req = req->nextRequestInList;
    cpt ++;
  }

  return req;
  
}


request * addToRequestQueue(request *list, request *requestToAdd) {
  request *origin = list;

  if (list == NULL) {
    return requestToAdd;
  }

  while(list->next != NULL) {
    list = list->next;
  }
  
  list->next = requestToAdd;

  return origin;
}

request * removeRequestFromList(request *list, request *requestToRemove) {
  request *origin = list;

  if (list == requestToRemove) {
    return list->next;
  }


  while(list->next != requestToRemove) {
    list = list->next;
  }

  list->next = requestToRemove->next;

  return origin;
} 


void copyParameters(request *src, request *dst) {
  int i;
  for(i=0; i<dst->nbOfParams; i++) {
    *(dst->params[i]) = *(src->params[i]);
  }
}


void clearListOfRequests(setOfRequests *list) {
  list->head = NULL;
}

setOfRequests *newListOfRequests(pthread_cond_t *wakeupCondition, pthread_mutex_t *mutex) {
  setOfRequests *list = (setOfRequests *)(malloc(sizeof(setOfRequests)));
  list->head = NULL;
  list->wakeupCondition = wakeupCondition;
  list->mutex = mutex;

  return list;
}

void fillListOfRequests(setOfRequests *list, pthread_cond_t *wakeupCondition, pthread_mutex_t *mutex) {
  list->head = NULL;
  list->wakeupCondition = wakeupCondition;
  list->mutex = mutex;
}


void addRequestToList(setOfRequests *list, request* req) {
  request *tmpreq;

  if (list == NULL) {
    criticalError("NULL List in addRequestToList");
  }

  if (req == NULL) {
    criticalError("NULL req in addRequestToList");
  }

  req->listOfRequests = list;

  if (list->head == NULL) {
    list->head = req;
    req->nextRequestInList = NULL;
    return;
  }

  tmpreq = list->head;
  while(tmpreq->nextRequestInList != NULL) {
    tmpreq = tmpreq->nextRequestInList;
  }

  tmpreq->nextRequestInList = req;
  req->nextRequestInList = NULL;
}
