#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

//ajoute DG
//#include <mutek/printk.h>
//#include <pthread.h>
//fin ajoute DG

//#include "pthread.h"
#include "mwmr.h"
#include "request.h"
#include "syncchannel.h"
#include "request_manager.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h"
//#include "srl.h"
//ajoute DG
//#include "srl_private_types.h"
//fin ajoute DG
#include "Door.h"
#include "Door.h"
#include "Bell.h"
#include "Magnetron.h"

#include "Controller.h"
#include "Controller.h"
#include "MicroWaveOven.h"
#include "ControlPanel.h"

//#include <cpu.h>
//#include "segmentation.h"

#define NB_PROC 6
#define WIDTH 4
#define DEPTH 16 

pthread_barrier_t barrier;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

//ajoute DG
//typedef struct srl_mwmr_status_s srl_mwmr_status_s;
//typedef struct srl_mwmr_lock_s srl_mwmr_lock_t;
//fin ajoute DG

void __user_init() {
}

#define MWMRd 0x60000000
#define LOCKS 0x30000000
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

  mwmr_t *Door_open__Controller_open   = (mwmr_t*)(base(MWMRd)+0x3000);
    mwmr_t  *Door_closed__Controller_closed   = (mwmr_t*)(base(MWMRd)+0x4000);
    mwmr_t *Door_okDoor__Controller_okDoor    = (mwmr_t*)(base(MWMRd)+0x2000);

/*************************Door*****************************/

    uint32_t *Door_open__fifo_data_in   = (uint32_t*)(base(MWMRd)+0x1000); //0x20200200;
    uint32_t *Door_closed__fifo_data_in   = (uint32_t*)(base(MWMRd)+0x2000); //0x20200400;
    uint32_t *Door_okDoor__fifo_data_out    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;

//    mwmr_t *Door_open__Controller_open 				__attribute__((section("section_mwmr0")))  = (mwmr_t*)(base(MWMRd)+0x3000);
//    mwmr_t  *Door_closed__Controller_closed  		__attribute__((section("section_mwmr1")))  = (mwmr_t*)(base(MWMRd)+0x4000);
//	mwmr_t *Door_okDoor__Controller_okDoor  		__attribute__((section("section_mwmr2")))   = (mwmr_t*)(base(MWMRd)+0x2000);

/***************************Bell*****************************/

    uint32_t *ringBell__fifo_data_in    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;   
//  mwmr_t *Controller_ringBell__Bell_ring  		__attribute__((section("section_mwmr3"))) = (mwmr_t*)(base(MWMRd)+0x2000);
   mwmr_t *Controller_ringBell__Bell_ring   = (mwmr_t*)(base(MWMRd)+0x2000);


/**********************Magnetron*****************************/

    uint32_t *startMagnetron__fifo_data_in    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;
    uint32_t *stopMagnetron__fifo_data_in   = (uint32_t*)(base(MWMRd)+0x1000); //0x20200200;

//   mwmr_t *Controller_startMagnetron__Magnetron_startM __attribute__((section("section_mwmr4"))) = (mwmr_t*)(base(MWMRd)+0x2000);
//    mwmr_t *Controller_stopMagnetron__Magnetron_stopM   __attribute__((section("section_mwmr5")))= (mwmr_t*)(base(MWMRd)+0x3000);

   mwmr_t *Controller_startMagnetron__Magnetron_startM = (mwmr_t*)(base(MWMRd)+0x2000);
   mwmr_t *Controller_stopMagnetron__Magnetron_stopM  = (mwmr_t*)(base(MWMRd)+0x3000);
 /*******************ControlPanel*****************************/


    uint32_t *startButton__fifo_data_out    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;
    uint32_t *LEDon__Controller_startCooking__fifo_data_out    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;
    uint32_t *LEDoff__Controller_stopCooking__fifo_data_out    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;
  
//  mwmr_t *ControlPanel_startButton__Controller_start   __attribute__((section("section_mwmr6"))) = (mwmr_t*)(base(MWMRd)+0x2000);
//  mwmr_t *ControlPanel_LEDon__Controller_startCooking  __attribute__((section("section_mwmr7")))= (mwmr_t*)(base(MWMRd)+0x2000);
//  mwmr_t *ControlPanel_LEDoff__Controller_stopCooking  __attribute__((section("section_mwmr8"))) = (mwmr_t*)(base(MWMRd)+0x2000);

    mwmr_t *ControlPanel_startButton__Controller_start   = (mwmr_t*)(base(MWMRd)+0x2000);
    mwmr_t *ControlPanel_LEDon__Controller_startCooking  = (mwmr_t*)(base(MWMRd)+0x2000);
    mwmr_t *ControlPanel_LEDoff__Controller_stopCooking  = (mwmr_t*)(base(MWMRd)+0x2000);


/*****************************Main****************************/

int main(int argc, char *argv[]) {
	void *ptr;
    pthread_barrier_init(&barrier,NULL, NB_PROC);
    pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
    pthread_attr_init(attr_t);
    pthread_mutex_init(&__mainMutex, NULL);

    /* Threads of tasks */ 
	
    pthread_t thread__MicroWaveOven;
    pthread_t thread__Bell;
    pthread_t thread__ControlPanel;
    pthread_t thread__Controller;
    pthread_t thread__Magnetron;
    pthread_t thread__Door;

	/* ici on initialise tous les canaux de l'application */

//Door

    static struct mwmr_status_s okDoor_status = MWMR_STATUS_INITIALIZER(  32,    2);
    static struct mwmr_status_s open_status = MWMR_STATUS_INITIALIZER(  32,    2);
    static struct mwmr_status_s closed_status = MWMR_STATUS_INITIALIZER(  32,    2);
   
//RG: ajouter le & a fin de respecter le prototype de la fonction
   struct mwmr_s  Door_okDoor__fifo_data_out  = MWMR_INITIALIZER( 32,2,Door_okDoor__Controller_okDoor, &okDoor_status, "Door_okDoor__fifo_data_out", MWMR_LOCK_INITIALIZER);
   struct mwmr_s  Door_open__fifo_data_in = MWMR_INITIALIZER(32,2,Door_open__Controller_open, &open_status, "Door_open__fifo_data_out", MWMR_LOCK_INITIALIZER);
   struct mwmr_s  Door_closed__fifo_data_in   = MWMR_INITIALIZER(32,2,Door_closed__Controller_closed, &closed_status, "Door_closed__fifo_data_out", MWMR_LOCK_INITIALIZER); 
    
//////////Bell

    static struct mwmr_status_s ringBell_status = MWMR_STATUS_INITIALIZER(  32,    2);
    struct mwmr_s  ringBell__fifo_data_in    = MWMR_INITIALIZER( 32,2,Controller_ringBell__Bell_ring, &ringBell_status, "ringBell__fifo_data_in", MWMR_LOCK_INITIALIZER);
  
//////////Magnetron

	static struct mwmr_status_s startMagnetron_status = MWMR_STATUS_INITIALIZER(  32,    2);
	static struct mwmr_status_s stopMagnetron_status = MWMR_STATUS_INITIALIZER(  32,    2);
  
	struct mwmr_s  startMagnetron__fifo_data_in  = MWMR_INITIALIZER( 32,2,Controller_startMagnetron__Magnetron_startM, &startMagnetron_status, "startMagnetron__fifo_data_in", MWMR_LOCK_INITIALIZER);
	struct mwmr_s  stopMagnetron__fifo_data_in = MWMR_INITIALIZER( 32,2,Controller_stopMagnetron__Magnetron_stopM, &stopMagnetron_status, "stopMagnetron__fifo_data_in", MWMR_LOCK_INITIALIZER);       
    
/////////ControlPanel

    static struct mwmr_status_s startButton_status = MWMR_STATUS_INITIALIZER(  32,    2);
    static struct mwmr_status_s LEDon_status = MWMR_STATUS_INITIALIZER(  32,   2);
    static struct mwmr_status_s LEDoff_status = MWMR_STATUS_INITIALIZER(  32,    2);
  
    struct mwmr_s  startButton__fifo_data_out    = MWMR_INITIALIZER( 32,2,ControlPanel_startButton__Controller_start, &startButton_status, "startButton__fifo_data_out", MWMR_LOCK_INITIALIZER);
    struct mwmr_s  LEDon__Controller_startCooking__fifo_data_out = MWMR_INITIALIZER(32,2,ControlPanel_LEDon__Controller_startCooking, &LEDon_status, "LEDOn__Controller_startCooking__fifo_data_out", MWMR_LOCK_INITIALIZER);
    struct mwmr_s  LEDoff__Controller_stopCooking__fifo_data_out = MWMR_INITIALIZER(32,2,ControlPanel_LEDoff__Controller_stopCooking, &LEDoff_status, "LEDoff__Controller_stopCooking__fifo_data_out", MWMR_LOCK_INITIALIZER);
   
   //initRandom();

  /* Initializing the main mutex */

  if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}


/* pour ce thread, qui ne lit ni ecrit les canaux, il est OK de transmettre uniquement son nom */
  ptr =malloc(sizeof(pthread_t));
  thread__MicroWaveOven = (pthread_t)ptr;
  
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   

  pthread_create(&thread__MicroWaveOven, attr_t, mainFunc__MicroWaveOven, (void *)"MicroWaveOven");

  /* le thread Bell utilise un seul canal*/
  ptr =malloc(sizeof(pthread_t));
  thread__Bell = (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;   
 
  pthread_create(&thread__Bell, attr_t, mainFunc__Bell, (void *)Controller_ringBell__Bell_ring);

 /* le thread ControlPanel utilise trois canaux*/
  struct mwmr_s  *canaux_panel[3];
  canaux_panel[0]=ControlPanel_LEDon__Controller_startCooking;
  canaux_panel[1]=ControlPanel_LEDoff__Controller_stopCooking;
  canaux_panel[2]=ControlPanel_startButton__Controller_start;
  
  ptr =malloc(sizeof(pthread_t));
  thread__ControlPanel = (pthread_t)ptr;

  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 2;   
  pthread_create(&thread__ControlPanel, attr_t, (void *)mainFunc__ControlPanel, (void *)canaux_panel);


/* le thread Controller utilise 9 canaux*/

  struct mwmr_s  *canaux_controller[9];
  canaux_controller[0]=Controller_ringBell__Bell_ring;
  canaux_controller[1]=Door_okDoor__Controller_okDoor; 
  canaux_controller[2]=Door_open__Controller_open;
  canaux_controller[3]=Door_closed__Controller_closed;
  canaux_controller[4]=Controller_startMagnetron__Magnetron_startM;
  canaux_controller[5]=Controller_stopMagnetron__Magnetron_stopM;
  //canaux_controller[6]=ControlPanel_LEDOn__Controller_startCooking;
  //canaux_controller[7]=ControlPanel_LEDoff__Controller_stopCooking;
  canaux_controller[8]=ControlPanel_startButton__Controller_start;

  ptr =malloc(sizeof(pthread_t));
  thread__Controller = (pthread_t)ptr;
  
  attr_t->cpucount = 3;  
  pthread_create(&thread__Controller, attr_t,(void *)mainFunc__Controller, (void *)canaux_controller);

/* le thread Magnetron utilise 2 canaux*/

  struct mwmr_s  *canaux_magnetron[2];
  canaux_magnetron[0]=Controller_startMagnetron__Magnetron_startM ;
  canaux_magnetron[1]=Controller_stopMagnetron__Magnetron_stopM;
	
  ptr =malloc(sizeof(pthread_t));
  thread__Magnetron = (pthread_t)ptr;

  attr_t->cpucount = 4;  
  pthread_create(&thread__Magnetron, attr_t, (void *)mainFunc__Magnetron, (void *)canaux_magnetron);


/* le thread Door utilise 3 canaux*/

 struct mwmr_s  *canaux_door[3];
  canaux_door[0]=Door_open__Controller_open;
  canaux_door[1]=Door_closed__Controller_closed;
  canaux_door[2]=Door_okDoor__Controller_okDoor;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Door = (pthread_t)ptr;

  attr_t = malloc(sizeof(pthread_attr_t));

  attr_t->cpucount = 5;  
  pthread_create(&thread__Door, attr_t, (void *)mainFunc__Door, (void *)canaux_door);
 
  pthread_join(thread__MicroWaveOven, NULL);
  pthread_join(thread__Bell, NULL);
  pthread_join(thread__ControlPanel, NULL);
  pthread_join(thread__Controller, NULL);
  pthread_join(thread__Magnetron, NULL);
  pthread_join(thread__Door, NULL);


  return 0;
}

