#ifndef game_h
#define game_h
#define MAX_LEN 128


void add(TREE*, TREE*);


void game(TREE* unit, TREE* root) {
    char* answr = (char*)malloc(0);
    
    if (unit->left) {
        puts(unit->val);
        answr = get_input();
        
        if (!strcmp(answr, "н")) {
            puts("");
            free(answr);
            game(unit->left, root);
            return;
        }
        else {
            puts("");
            free(answr);
            game(unit->right, root);
            return;
        }
    }
    
    puts(unit->val);
    puts("Вы загадали это животное? д/н (да/нет)");
    answr = get_input();
    
    if (!strcmp(answr, "д")) {
        puts("Я победил!");
        free(answr);
        return;
    }
    else {
        free(answr);
        add(unit, root);
        return;
    }
}


void add(TREE* unit, TREE* root) {
    char* question = (char*)malloc(0);
    char* answr = (char*)malloc(0);
    char* val = (char*)malloc(0);
    
//    get information for addition a new unit
    puts("Какое животное вы загадали? (в единственном числе, им. падеже)");
    val = get_input();
    puts("Каким вопросом можно его отличить от предложенного животного?");
    question = get_input();
    puts("Какой ответ должен быть ответ? д/н (да/нет)");
    answr = get_input();
    
    if (!strcmp(answr, "д")) {
        unit->right = init(val);
        unit->left = init(unit->val);
        unit->val = question;
    }
    else {
        unit->left = init(val);
        unit->right = init(unit->val);
        unit->val = question;
    }
    
//    update file, addition new question and animal
    FILE* file = fopen("source.txt", "w");
    rewrite_file(root, file);
    fclose(file);
    puts("Ваше животно сохранено!\nВы выйграли!");
    
    free(answr);
}


#endif /* game_h */
