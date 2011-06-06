#include <stdlib.h>
#include <stdio.h>
#include <time.h>

#include "tracemanager.h"
#include "debug.h"
#include "mytimelib.h"


#define TRACE_OFF 0
#define TRACE_IN_FILE 1

#define TRACE_FILE_NAME "Trace.txt"


//pthread_mutex_t traceMutex;
//pthread_cond_t wakeupTraceManager;

int trace = TRACE_OFF;
int id = 0;

FILE *file;

struct timespec begints;



void addInfo(char *dest, char *info) {
  char s1[10];
  long tmp;
  long tmp1;
  int i;
  struct timespec ts, ts1;
  my_clock_gettime(&ts);
  
  debugMsg("DIFF TIME");
  diffTime(&begints, &ts, &ts1);

  tmp = ts1.tv_nsec;

  if (tmp < 0) {
    tmp = -tmp;
  }

  tmp1 = 100000000;

  for(i=0; i<9; i++) {
    s1[i] = 48 + (tmp / tmp1);
    tmp = tmp % tmp1;
    tmp1 = tmp1 / 10;
  }
  s1[9] = '\0';
  
  sprintf(dest, "#%d time=%ld.%s %s", id, ts1.tv_sec, s1, info);
  id ++;
}


void writeInTrace(char *info) {
  char s[CHAR_ALLOC_SIZE];
  addInfo(s, info);
		 //printf("Write in file\n");
  if (file != NULL) {
    

    debug2Msg("Saving in file", s);
    fprintf(file, s);
    fflush(file);
  }
}


void activeTracingInFile(char *fileName) {
  char *name;
  trace = TRACE_IN_FILE;
  my_clock_gettime(&begints); 
  if (fileName == NULL) {
    name = TRACE_FILE_NAME;
  } else {
    name  = fileName;
  }
  file = fopen(name,"w");
}

void unactiveTracing() {
  trace = TRACE_OFF;
}


void traceStateEntering(char *myname, char *statename) {
  char s[CHAR_ALLOC_SIZE];

  debugMsg("Trace function");

  if (trace == TRACE_OFF) {
    return;
  }

  sprintf(s, "block=%s type=state_entering state=%s\n", myname, statename);

  // Saving trace
  writeInTrace(s);
}

void traceFunctionCall(char *block, char *func, char *params) {
  char s[CHAR_ALLOC_SIZE];

  debugMsg("Trace function");

  if (trace == TRACE_OFF) {
    return;
  }

  sprintf(s, "block=%s type=function_call func=%s parameters=%s\n", block, func, params);

  // Saving trace
  writeInTrace(s);
}


// type=0: int type = 1:bool
void traceVariableModification(char *block, char *var, int value, int type) {
  char s[CHAR_ALLOC_SIZE];
  debugMsg("Trace variable modification");

  if (trace == TRACE_OFF) {
    return;
  }

  
  if (type == 0) {
    sprintf(s, "block=%s type=variable_modification variable=%s setTo=%d\n", block, var, value);
  }

  if (type == 1) {
    if (value == 0) {
      sprintf(s, "block=%s type=variable_modification variable=%s setTo=false\n", block, var);
    } else {
      sprintf(s, "block=%s type=variable_modification variable=%s setTo=true\n", block, var);
    }
  }

  // Saving trace
  writeInTrace(s);

}


void traceRequest(char *myname, request *req) {
  char s[1024];

  debugMsg("Trace request");


  if (trace == TRACE_OFF) {
    return;
  }

  // Build corresponding char*;

  switch(req->type) {
  case SEND_SYNC_REQUEST:
    debug2Msg("Sync channel", req->syncChannel->outname);
    if ( req->relatedRequest == NULL) {
      printf("NULL related\n");
    }
    sprintf(s, "block=%s type=send_synchro channel=%s blockdestination=%s\n", myname, req->syncChannel->outname, req->relatedRequest->listOfRequests->owner);
    break;
  case RECEIVE_SYNC_REQUEST:
    sprintf(s, "block=%s type=receive_synchro channel=%s\n", myname, req->syncChannel->inname);
    break;
   case SEND_ASYNC_REQUEST:
    debug2Msg("Async channel", req->asyncChannel->outname);
    sprintf(s, "block=%s type=send_async channel=%s\n", myname, req->asyncChannel->outname);
    break;
   case RECEIVE_ASYNC_REQUEST:
    sprintf(s, "block=%s type=receive_async channel=%s\n", myname, req->asyncChannel->inname);
    break;
   case SEND_BROADCAST_REQUEST:
    debug2Msg("Sync channel", req->syncChannel->outname);
    sprintf(s, "block=%s type=send_broadcast channel=%s\n", myname, req->syncChannel->outname);
    break; 
   case RECEIVE_BROADCAST_REQUEST:
    debug2Msg("Sync channel", req->syncChannel->outname);
    sprintf(s, "block=%s type=receive_broadcast channel=%s\n", myname, req->syncChannel->outname);
    break; 
   case IMMEDIATE:
     sprintf(s, "block=%s type=action\n", myname);
    break;
  default:
    sprintf(s, "block=%s type=unknown\n", myname);
  }

  debugMsg("Trace request 2");
  

  // Saving trace
  writeInTrace(s);
}
