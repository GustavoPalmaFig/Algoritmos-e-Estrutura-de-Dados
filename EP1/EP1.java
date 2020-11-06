#include <stdlib.h>
#include <stdio.h>
#define true 1
#define false 0
#define IDADEPREFERENCIAL 60

typedef int bool;

typedef struct aux {
  int id;
  int idade;
  struct aux* prox;
} ELEMENTO, * PONT;

typedef struct {
  PONT inicio;
  PONT fimPref;
  PONT inicioNaoPref;
  PONT fim;
} FILAPREFERENCIAL, * PFILA;


PFILA criarFila(){
	PFILA res = (PFILA) malloc(sizeof(FILAPREFERENCIAL));
	res->inicio = NULL;
	res->fimPref = NULL;
	res->inicioNaoPref = NULL;
	res->fim = NULL;
	return res;
}

int tamanho(PFILA f){
	PONT atual = f->inicio;
	int tam = 0;
	while (atual) {
    	atual = atual->prox;
    	tam++;
	}
	return tam;
}

PONT buscarID(PFILA f, int id){
	PONT atual = f->inicio;
 	while (atual) {
    	if (atual->id == id) return atual;
    	atual = atual->prox;
	}
	return NULL;
}

void exibirLog(PFILA f){
	int numElementos = tamanho(f);
	printf("\nLog fila [elementos: %i] - Inicio:", numElementos);
	PONT atual = f->inicio;
	while (atual){
    	printf(" [%i;%i]", atual->id, atual->idade);
    	atual = atual->prox;
	}
	printf("\n\n");
}


int consultarIdade(PFILA f, int id){
	PONT atual = f->inicio;
 	while (atual) {
    	if (atual->id == id) return atual->idade;
    	atual = atual->prox;
	}
	return -1;
}



bool inserirPessoaNaFila(PFILA f, int id, int idade){
    PONT atual = buscarID(f, id);
	if (id<0 || idade<0 || atual != NULL) return false;
	PONT novo = (PONT) malloc(sizeof(ELEMENTO));
	novo->id = id;
	novo->idade = idade;

	if(f->inicio == NULL){
    	f->inicio = novo;
    	f->fim = novo;
    	novo->prox = NULL;
    	if (idade>=IDADEPREFERENCIAL) f->fimPref = novo;
    	else f->inicioNaoPref = novo;
	}
	else{
    	if(!f->fimPref){ //há pessoas somente não preferenciais na fila (fimPref==NULL)
        	if (idade>=IDADEPREFERENCIAL){
            	f->inicio = novo;
            	f->fimPref = novo;
            	novo->prox = f->inicioNaoPref;
        	} else {
            	f->fim->prox = novo;
            	f->fim = novo;
            	novo->prox = NULL;
        	}
    	}
    	else if (!f->inicioNaoPref){ //há somente pessoas preferenciais na fila
        	if (idade>=IDADEPREFERENCIAL){
            	f->fimPref = novo;
            	f->fim->prox = novo;
            	f->fim = novo;
            	novo->prox = NULL;
        	} else {
            	f->inicioNaoPref = novo;
            	f->fim->prox = novo;
            	f->fim = novo;
            	novo->prox = NULL;
        	}
    	}
    	else { //há pessoas ao longo de toda a fila
        	if (idade>=IDADEPREFERENCIAL){
            	f->fimPref->prox = novo;
            	f->fimPref = novo;
            	novo->prox = f->inicioNaoPref;
        	} else {
            	f->fim->prox = novo;
            	f->fim = novo;
            	novo->prox = NULL;
        	}
    	}
	}

	return true;
}



bool atenderPrimeiraDaFila(PFILA f, int* id){
	if(f->inicio){
    	*id = f->inicio->id;
    	PONT atender = f->inicio; //guardando ponteiro do elemenento no início da fila
    	if(f->inicio==f->fim){ //fila somente com uma pessoa
        	f->inicio = NULL;
        	f->fimPref = NULL;
        	f->inicioNaoPref = NULL;
        	f->fim = NULL;
    	} else  f->inicio = atender->prox;
    	
    	free(atender);
    	return true;
	}
	return false;
}

PONT buscaSeqExc(PFILA f, int id, PONT* ant){
	*ant = NULL;
	PONT atual = f->inicio;
	while (atual != NULL && (atual->id) != id){
    	*ant = atual;
    	atual = atual->prox;
	}
	if ((atual != NULL) && ((atual->id) == id)) return atual;
	return NULL;
}

bool desistirDaFila(PFILA f, int id){
	if(buscarID(f,id)){
    	PONT ant;
    	PONT elementoDesistente = buscarID(f,id);
     	if(f->inicio==elementoDesistente){
         	atenderPrimeiraDaFila(f,&id);
        	return true;
    	}
    	else {
        	buscaSeqExc(f, id, &ant);
        	if(elementoDesistente==f->fimPref){
            	if(elementoDesistente->prox->idade>=IDADEPREFERENCIAL) f->fimPref=elementoDesistente->prox;
            	else f->fimPref=ant;
            	ant->prox = elementoDesistente->prox;
            	free(elementoDesistente);
            	return true;
        	}
        	else if(elementoDesistente==f->inicioNaoPref){
            	if(f->inicioNaoPref==f->fim) {
                	f->inicioNaoPref=NULL;
                	f->fim=ant;

            	} else{
                	f->inicioNaoPref=elementoDesistente->prox;
            	}
            	ant->prox = elementoDesistente->prox;
            	free(elementoDesistente);
            	return true;
        	}
        	else if(elementoDesistente==f->fim){
            	f->fim=ant;
            	ant->prox = NULL;
            	free(elementoDesistente);
            	return true;
        	}
        	//a partir daqui é novo
        	else {
        		ant->prox = elementoDesistente->prox;
        		free(elementoDesistente);
        		return true;
        	}
    	}
	}
	return false;
}


int main() {
	PFILA f = criarFila();
	int id;
	int idade;
	bool res;

	printf("################# INSERINDO #######################\n");

	exibirLog(f);
	res = inserirPessoaNaFila(f, -1, -2);
	if(res) printf("Insercao retornou true (0).\n");
	else printf("Insercao retornou false (0). [OK]\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 1, 21);
	if(res) printf("Insercao retornou true (1). [OK]\n");
	else printf("Insercao retornou false (1).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 2, 11);
	if(res) printf("Insercao retornou true (2). [OK]\n");
	else printf("Insercao retornou false (2).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 3, 31);
	if(res) printf("Insercao retornou true (3). [OK]\n");
	else printf("Insercao retornou false (3).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 4, 71);
	if(res) printf("Insercao retornou true (4). [OK]\n");
	else printf("Insercao retornou false (4).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 5, 61);
	if(res) printf("Insercao retornou true (5). [OK]\n");
	else printf("Insercao retornou false (5).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 6, 81);
	if(res) printf("Insercao retornou true (6). [OK]\n");
	else printf("Insercao retornou false (6).\n");
	exibirLog(f);


	printf("################# ATENDENDO #######################\n");
	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (7), id=%i. [OK]\n",id);
	else printf("Atendimento retornou false (7).\n");

	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (8), id=%i. [OK]\n",id);
	else printf("Atendimento retornou false (8).\n");

	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (9), id=%i. [OK]\n",id);
	else printf("Atendimento retornou false (9).\n");

	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (10), id=%i. [OK]\n",id);
	else printf("Atendimento retornou false (10).\n");

	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (11), id=%i. [OK]\n",id);
	else printf("Atendimento retornou false (11).\n");

	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (12), id=%i. [OK]\n",id);
	else printf("Atendimento retornou false (12).\n");

	exibirLog(f);
	res = atenderPrimeiraDaFila(f, &id);
	if(res) printf("Atendimento retornou true (13), id=%i.\n",id);
	else printf("Atendimento retornou false (13). [OK]\n");
	exibirLog(f);



	printf("################# INSERINDO PARTE 2 ###############\n");

	exibirLog(f);
	res = inserirPessoaNaFila(f, 7, 72);
	if(res) printf("Insercao retornou true (14). [OK]\n");
	else printf("Insercao retornou false (14).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 8, 22);
	if(res) printf("Insercao retornou true (15). [OK]\n");
	else printf("Insercao retornou false (15).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 9, 60);
	if(res) printf("Insercao retornou true (16). [OK]\n");
	else printf("Insercao retornou false (16).\n");
	exibirLog(f);
	res = inserirPessoaNaFila(f, 10, 42);
	if(res) printf("Insercao retornou true (17). [OK]\n");
	else printf("Insercao retornou false (17).\n");
	exibirLog(f);


	printf("################# SAINDO DA FILA ##################\n");

	exibirLog(f);
	res = desistirDaFila(f, 6);
	if(res) printf("Desistindo da fila retornou true (18).\n");
	else printf("Desistindo da fila retornou false (18). [OK]\n");
	exibirLog(f);
	res = desistirDaFila(f, 7);
	if(res) printf("Desistindo da fila retornou true (19). [OK]\n");
	else printf("Desistindo da fila retornou false (19).\n");
	exibirLog(f);
	res = desistirDaFila(f, 8);
	if(res) printf("Desistindo da fila retornou true (20). [OK]\n");
	else printf("Desistindo da fila retornou false (20).\n");
	exibirLog(f);
	res = desistirDaFila(f, 9);
	if(res) printf("Desistindo da fila retornou true (21). [OK]\n");
	else printf("Desistindo da fila retornou false (21).\n");
	exibirLog(f);
	res = desistirDaFila(f, 10);
	if(res) printf("Desistindo da fila retornou true (22). [OK]\n");
	else printf("Desistindo da fila retornou false (22).\n");
	exibirLog(f);

	return 0;
}






