#ifndef queue_h
#define queue_h


typedef struct queue {
    NODE *node;
    long priority;
    struct queue *next;
    struct queue *prev;
} QUEUE;


QUEUE *initQueue (QUEUE *queue, NODE *node, QUEUE *next, QUEUE *prev) {
    queue = (QUEUE*)malloc(sizeof(QUEUE));
    queue->node = node;
    queue->priority = node->count;
    queue->prev = prev;
    queue->next = next;
    
    return queue;
}


QUEUE *addToQueue(QUEUE *queue, NODE *node, QUEUE *next, QUEUE *prev) {
    if (!queue)
        queue = initQueue(queue, node, next, prev);
        
    else if (queue->priority < node->count) {
        if (queue->next && queue->next->priority > node->count) {
            QUEUE *tmp = queue->next;
            queue->next = addToQueue(NULL, node, tmp, queue);
            tmp->prev = queue->next;
        }
        else {
            QUEUE *tmp = queue;
            queue = addToQueue(queue->next, node, next, tmp);
            if (!tmp->next)
                tmp->next = queue;
        }
    }
    else {
        if (queue->prev && queue->prev->priority < node->count) {
            QUEUE *tmp = queue->prev;
            queue->prev = addToQueue(NULL, node, queue, tmp);
            tmp->next = queue->prev;
        }
        else {
            QUEUE *tmp = queue;
            queue = addToQueue(queue->prev, node, tmp, prev);
            if (!tmp->prev)
                tmp->prev = queue;
        }
    }
    return queue;
}


QUEUE *pop (QUEUE *queue) {
    if (queue->prev)
        queue->prev->next = queue->next;
    if (queue->next)
        queue->next->prev = queue->prev;
    
    if (queue->next) {
        QUEUE *tmp = queue->next;
        free(queue);
        return tmp;
    }
    else {
        QUEUE *tmp = queue->prev;
        free(queue);
        return tmp;
    }
}

#endif
