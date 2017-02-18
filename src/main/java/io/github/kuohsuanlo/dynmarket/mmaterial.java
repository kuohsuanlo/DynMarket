package io.github.kuohsuanlo.dynmarket;

class mmaterial{
	int id;
	int mpool_number[];
	public mmaterial(int nid, int[] mn){
		id= nid;
		for(int i=0;i<16;i++){
			this.mpool_number = mn;
		}
	}
	
}