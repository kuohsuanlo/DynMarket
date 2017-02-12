package io.github.kuohsuanlo.dynmarket;

class mmaterial{
	int id;
	int data[];
	double price[];
	int mpool_number[];
	public mmaterial(int nid, double[] np, int[] mn){
		id= nid;
		data = new int[16];
		for(int i=0;i<16;i++){
			this.price= np;
			this.mpool_number = mn;
		}
	}
	
}