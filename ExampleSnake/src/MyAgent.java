import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStreamReader;
import za.ac.wits.snake.DevelopmentAgent;
import java.util.LinkedList;
import java.util.Random;

public class MyAgent extends DevelopmentAgent {
	int[][] gamebounds;int[][] snakeHeads;
    String[][] snakeState;
    
    int shawnsnake;
    int[] redapple; int[] blueapple; int[] controls;
    
    //controls  0 is Up, 1 is Down, 2 is Left, 3 is Right
	int b, a;
	FileWriter lkt;
    BufferedWriter sao;
    
    
    
    boolean currentwhite = false;
    boolean previouswhite = false;
    String[] invispos;
  //invisible snake after eating blue apple  
    int whitesnakenumber;
    
    public static void main(String args[]) throws IOException {
    	
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }
    
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            
            String[] temp = initString.split(" ");
            int nSnakes = Integer.parseInt(temp[0]);
    		b = Integer.parseInt(temp[1]);
    		a = Integer.parseInt(temp[2]);
    		gamebounds = new int[b][a];
    		
            while (true) {
            	String state[] = new String[7];
                state[0] = br.readLine();
                state[1] = br.readLine();
                state[2] = br.readLine();
                state[3] = br.readLine();
                state[4] = br.readLine();
                state[5] = br.readLine();
                state[6] = br.readLine();
                //logState(state);
                
                snakeState = new String[4][];
                int l = 0;
                while( l < 4){
                	snakeState[l] = state[3+l].split(" ");
                	l++;
                }
                
              //initialize game bounds 
                int t = 0;
                while ( t < b){	
        			for (int j = 0; j < a; j++){
        				gamebounds[t][j] = 0;
        			}
        			t++;
        		}
                
                if (state[0].contains("Game Over")) {
                    break;
                }
                
                redapple = new int[2]; blueapple = new int[2];
                
                String[] redapple1 = state[0].split(" ");String[] blueapple1 = state[1].split(" ");
                
                redapple[0] = Integer.parseInt(redapple1[0]);  redapple[1] = Integer.parseInt(redapple1[1]);
                blueapple[0] = Integer.parseInt(blueapple1[0]);	blueapple[1] = Integer.parseInt(blueapple1[1]);
                
                
              //Sets both apple positions to -1 on the game bound
                if ((redapple[0] >= 0)); 
                	if ((redapple[1] >= 0)) {gamebounds[redapple[1]][redapple[0]] = -1;}
                	
                	
                if ((blueapple[0] >= 0)); 
                	if ((blueapple[1] >= 0)) {gamebounds[blueapple[1]][blueapple[0]] = -1;}
           
                controls = new int[4];          
                shawnsnake = Integer.parseInt(state[2]);
                
              //to store positions of the heads of snakes 
                snakeHeads = new int[nSnakes][2]; 	
                previouswhite = currentwhite;
                currentwhite = false;
                whitesnakenumber = -1;
                
                
                
                
                int z = 0;
                while ( z < nSnakes ) {
                	// {state, length, kills, {points}}
                    String[] snakeLine = snakeState[z]; 	
                    
                    if (!snakeLine[0].equals("dead")){
                    	String[] str;
                    	if (!snakeLine[0].equals("invisible")) {
                    		str = Arrays.copyOfRange(snakeLine, 3, snakeLine.length);
                    	} 
                    	else {
                    		if (z != shawnsnake) {
                    			currentwhite = true;
                    			whitesnakenumber = z;
                    			if (!previouswhite){
                    				invispos = snakeLine;
                    			}
                    			str = Arrays.copyOfRange(invispos, 5, snakeLine.length);
                    		}
                    		else{
                    			str = Arrays.copyOfRange(snakeLine, 5, snakeLine.length);
                    		}
                    	}
                    	
                    	if (str.length > 1) {
                    		String[] head = str[0].split(",");
	                    	String[] first = str[1].split(",");
	                    	if (Integer.parseInt(head[1]) < Integer.parseInt(first[1]))	
	                    		{controls[z] = 0;}
	                    	else if (Integer.parseInt(head[1]) > Integer.parseInt(first[1]))	
	                    		{controls[z] = 1;}
	                    	else if (Integer.parseInt(head[0]) < Integer.parseInt(first[0]))	
	                    		{controls[z] = 2;}
	                    	else if (Integer.parseInt(head[0]) > Integer.parseInt(first[0]))	
	                    		{controls[z] = 3;}
                    	}
                    	int j = 0;
                        while ( j < str.length ){	//fill in snake bodies	
            				String[] coord = str[j].split(",");
            				if (j == 0) {
            					snakeHeads[z][0] = Integer.parseInt(coord[0]);
            					snakeHeads[z][1] = Integer.parseInt(coord[1]);
            				}
            				
            				gamebounds[Integer.parseInt(coord[1])][Integer.parseInt(coord[0])] = 1;
            				
            				if (j < str.length-1) {
            					String[] next = str[j+1].split(",");
            					
            					if (coord[0].equals(next[0])){
            						
            						for (int k = Math.min(Integer.parseInt(coord[1]), Integer.parseInt(next[1])); k < Math.max(Integer.parseInt(coord[1]), Integer.parseInt(next[1])); k++){
            							gamebounds[k][Integer.parseInt(coord[0])] = 1;
            						}
            					}
            					
            					if (coord[1].equals(next[1])){
            						for (int k = Math.min(Integer.parseInt(coord[0]), Integer.parseInt(next[0])); k < Math.max(Integer.parseInt(coord[0]), Integer.parseInt(next[0])); k++){
            							gamebounds[Integer.parseInt(coord[1])][k] = 1;
            						}
            					}
            				}
            				
            				
            				j++;
            			}
                    }
                    z++;
                }
                
                int move = safemove();
                System.out.println(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
  //performs a BFS from start to stop and returns an ArrayList with moves to make to the apple (stop)
    
    private ArrayList<int[]> bestfs(int[] start, int[] stop, int[][] map){	
    	if ((start[0] < 0) || (start[0] > b-1) || (start[1] < 0) || (start[1] > a-1)){
    		return new ArrayList<int[]>();
    	}
    	LinkedList<int[]> q = new LinkedList<int[]>();
    	int[][][] parent = new int[a][b][2];
    	boolean[][] checked = new boolean[a][b];
    	for (int i = 0; i < b; i++){
			for (int j = 0; j < a; j++){
				checked[i][j] = false;
				parent[i][j][0] = -1;
				parent[i][j][1] = -1;
			}
		}
    	
    	checked[start[1]][start[0]] = true;
    	q.addLast(start);
    	int[] current = start;
    	
    	while ((!q.isEmpty()) && (!Arrays.equals(current,stop))){
    		current = q.remove();
    		
    		int[] down = new int[2];
    		down[0] = current[0];
    		down[1] = current[1]+1;
    		
    		int[] up = new int[2];
    		up[0] = current[0];
    		up[1] = current[1]-1;	
    		
    		int[] right = new int[2];
    		right[0] = current[0]+1;
    		right[1] = current[1];	
    		
    		int[] left = new int[2];
    		left[0] = current[0]-1;
    		left[1] = current[1];
    	
    		if ((down[1] < a)) 
    			if (checked[down[1]][down[0]] == false) 
    				if ((map[down[1]][down[0]] == 0 || map[down[1]][down[0]] == -1)){
    					checked[down[1]][down[0]] = true;
    					parent[down[1]][down[0]] = current;
    					q.addLast(down);
    		}
    		
    		if ((up[1] >= 0)) 
    			if (checked[up[1]][up[0]] == false) 
    				if ((map[up[1]][up[0]] == 0 || map[up[1]][up[0]] == -1)){
    					checked[up[1]][up[0]] = true;
    					parent[up[1]][up[0]] = current;
    					q.addLast(up);
    		}
    		
    		if ((right[0] < b)) 
    			if (checked[right[1]][right[0]] == false) 
    				if ((map[right[1]][right[0]] == 0 || map[right[1]][right[0]] == -1)){
    			
    					checked[right[1]][right[0]] = true;   			
    					parent[right[1]][right[0]] = current;
    					q.addLast(right);
    		}
    		
    		if ((left[0] >= 0)) 
    			if (checked[left[1]][left[0]] == false) 
    				if ((map[left[1]][left[0]] == 0 || map[left[1]][left[0]] == -1)){
    					checked[left[1]][left[0]] = true;
    					parent[left[1]][left[0]] = current;
    					q.addLast(left);
    		}
    	} 
    	
    	ArrayList<int[]> arr = new ArrayList<int[]>();
    	if (!q.isEmpty()) {
    		if (checked[stop[1]][stop[0]]) {
    			while (!Arrays.equals(current,start)) {
    				arr.add(current);
    				current = parent[current[1]][current[0]];
    		};
    	}
    }
    	return arr;
    }
    
    
    

    
    private boolean movetofood(int[] head, int[] food){
    	if (((head[1] + 1 == food[1]) 
    		&& (head[0] == food[0])) ||	//food is a move down
    		 (head[1] - 1 == food[1]) 
    		 && (head[0] == food[0])  ||	//food is a move up
    		 (head[0] - 1 == food[0]) 
    		 && (head[1] == food[1])  ||	//food is a move left
    		 (head[0] + 1 == food[0]) 
    		 && (head[1] == food[1]))  {	//food is a move right
    		return true;
    	}
    	else return false;
    }
    
    
    
    
    
    
    private void logState(String[] state) throws IOException{
        StringBuilder sb = new StringBuilder();
        for (String s : state) {
            sb.append(s).append("\n");
        }
        lkt = new FileWriter("out.txt", true);
        sao = new BufferedWriter(lkt);
		sao.write(sb.toString());
		sao.close();
		lkt.close();
    }
    
    private int safemove(){
    	int[] position = snakeHeads[shawnsnake];
    	int m = new Random().nextInt(4);	//if there's no path to the apple, make a random move
    	boolean clash = false;
    	ArrayList<int[]> moves, bluemoves;
    	int[] nextsafemove;
    	boolean homealone = ((redapple[0] != -1) && ((redapple[1] != -1)));
    	boolean eatredapple = false;
    	
    	if (currentwhite){
    		String[] t = snakeState[shawnsnake][snakeState[shawnsnake].length-1].split(",");
    		int[] tail = {Integer.parseInt(t[0]), Integer.parseInt(t[1])};
    		gamebounds[tail[1]][tail[0]] = 0;
    		moves = bestfs(position, tail, gamebounds);
    		
    		if (moves.size() > 2) {
        		nextsafemove = (moves.get(moves.size()-1));
        		if (nextsafemove[1] - position[1] == 1)	
        			{m = 1;}	//DOWN
    	    	if (nextsafemove[1] - position[1] == -1)	
    	    		{m = 0;}	//UP
    	    	if (nextsafemove[0] - position[0] == 1)	
    	    		{m = 3;}	//RIGHT
    	    	if (nextsafemove[0] - position[0] == -1)	
    	    		{m = 2;}	//LEFT
        	}	
    		//if there's no path, try not to die
    			else {	
        		gamebounds[tail[1]][tail[0]] = 1;
        		m = detourmove(position);
        	}
    		nextsafemove = destinationFromInitialMove(m, position);
    		for (int i = 0; i < 4; i++){
        		if (i != shawnsnake){
        			int[] nextmove = destinationFromInitialMove(controls[i], snakeHeads[i]);
        			//gets the next move if snake continues straight
        			if (Arrays.equals(nextsafemove, nextmove)) {
    					gamebounds[nextmove[1]][nextmove[0]] = 1;
    					m  = detourmove(position);
    				}
        		}
    		}
    		return m;
    	}
    	
    	if (homealone) eatredapple= movetofood(snakeHeads[shawnsnake], redapple);
    	if (movetofood(snakeHeads[shawnsnake], blueapple) || eatredapple){	
        	for (int i = 0; i < 4; i++){
        		if (i != shawnsnake){
        			if ((movetofood(snakeHeads[i], blueapple)))
        					if(Integer.parseInt(snakeState[i][1]) <= Integer.parseInt(snakeState[shawnsnake][1])){
        				clash = true;
        			}
        		}
        	}
        }
    	
    	if (!clash){
    		moves = bestfs(position, blueapple, gamebounds);
    		//prioritise invisibility apple if it takes less half the board
    		if (homealone) {	
    			bluemoves = bestfs(position, redapple, gamebounds);
    			moves = (moves.size() < bluemoves.size() - 20) ? moves : bluemoves;	
    		}
    		
    		if (moves.size() > 0) {
        		nextsafemove = (moves.get(moves.size()-1));
        		if (nextsafemove[1] - position[1] == 1)	
        			{m = 1;}	//DOWN
    	    	if (nextsafemove[1] - position[1] == -1)	
    	    		{m = 0;}	//UP
    	    	if (nextsafemove[0] - position[0] == 1)	
    	    		{m = 3;}	//RIGHT
    	    	if (nextsafemove[0] - position[0] == -1)	
    	    		{m = 2;}	//LEFT
        	}	
    		//if there's no path, try not to die
    			else {	
        		m = detourmove(position);
        	}
    		
    		nextsafemove = destinationFromInitialMove(m, position);
    		boolean redo = false;
    		boolean centralize = false;
    		int[] centre = tailattraction();
    		for (int i = 0; i < 4; i++){
        		if (i != shawnsnake){
        			//gets the next move if snake continues straight
        			int[] nextmove = destinationFromInitialMove(controls[i], snakeHeads[i]);	
        			if (Arrays.equals(nextsafemove, nextmove)) {
        				if (!crashAvoidance(nextsafemove, snakeHeads[i], controls[i])){
        					gamebounds[nextmove[1]][nextmove[0]] = 1;
        				}
    					redo = true;
    				}
    				ArrayList<int[]> a = bestfs(snakeHeads[i], blueapple, gamebounds);
    				if (a.size() + 15 < moves.size()) 
    					if (Integer.parseInt((snakeState[shawnsnake][1])) < 45) {
    						redo = true;
    						centralize = true;
    				}
    				if (crashAvoidance(nextsafemove, snakeHeads[i], controls[i])){
    					redo = true;
    				}
        		}
        	}
    		boolean another = false;
    		if (redo){
    			moves = bestfs(position, centre, gamebounds);
    			ArrayList<int[]> centremoves = bestfs(position, blueapple, gamebounds);
    			int movesize = moves.size();
    			if (centralize) 
    					if(movesize < 4) {
    				moves = centremoves;
    			}
    			else m = detourmove(position);
    			
        		if (moves.size() > 0) {
            		nextsafemove = (moves.get(moves.size()-1));
            		if (nextsafemove[1] - position[1] == 1)	
            			{m = 1;}	//DOWN
        	    	if (nextsafemove[1] - position[1] == -1)	
        	    		{m = 0;}	//UP
        	    	if (nextsafemove[0] - position[0] == 1)	
        	    		{m = 3;}	//RIGHT
        	    	if (nextsafemove[0] - position[0] == -1)	
        	    		{m = 2;}	//LEFT
            	}	
        		//Crash avoidance if no safe move
        			else {	
            		another = true;
            	}
        		nextsafemove = destinationFromInitialMove(m, position);
        		for (int i = 0; i < 4; i++){
            		if (i != shawnsnake){
            			int[] nextmove = destinationFromInitialMove(controls[i], snakeHeads[i]);	
            			if (Arrays.equals(nextsafemove, nextmove)) {
            				if (!crashAvoidance(nextsafemove, snakeHeads[i], controls[i])){
            					gamebounds[nextmove[1]][nextmove[0]] = 1;
            				}
        					another = true;
        				}
            			if (centralize){
            				int[][] tempmap = gamebounds;
            				tempmap[nextsafemove[0]][nextsafemove[1]] = 1;
            				ArrayList<int[]> nexttings = bestfs(position, blueapple, tempmap);
            				if (nexttings.isEmpty()) {
            					another = true;
            				}
            			}
            		}
            	}
    		}
    		if (another) m = detourmove(position);
    	}
    	else {
    		//crash avoidance when eating apple
    		m = detourmove(position);	
    	}
    	
    	return m;
    }
    

    
    private int[] destinationFromInitialMove(int m, int[] livelocation){
    	int x = livelocation[0];	int y = livelocation[1];
    	switch (m){
    		case 0:	
    			y = y - 1;
    			break;
    		case 1:	
    			y = y + 1;
    			break;
    		case 2:	
    			x = x - 1;
    			break;
    		case 3:	
    			x = x + 1;
    			break;
    	}
    	int[] pos = {x, y};
    	return pos;
    }
    
    private	boolean crashAvoidance(int[] snakemove, int[] rest, int bearing){
    	int x = rest[0]; int y = rest[1];
    	if ((x-1 < 0) || (x+1 >= b) || (y-1 < 0) || (y+1 >= a)) return false;
    	boolean onlysafemove = false;
    	int[] move = rest;
    	switch (bearing){
    	
    	
    	//Facing Up
    		case 0:{	
    			if 	((gamebounds[y+1][x] == 0)) 
    					if(gamebounds[y][x-1] == 1) 
    						if((gamebounds[y][x+1] == 1))	{
    							move[1] = y+1;
    							onlysafemove = true;
    			}
    			
    			
    			else if ((gamebounds[y+1][x] == 1)) 
    					if (gamebounds[y][x-1] == 0) 
    						if((gamebounds[y][x+1] == 1)) {
    							move[0] = x-1;
    							onlysafemove = true;
    			}
    			else if ((gamebounds[y+1][x] == 1)) 
    				if (gamebounds[y][x-1] == 1) 
    					if ((gamebounds[y][x+1] == 0)) {
    						move[0] = x+1;
    						onlysafemove = true;
    			}
    		}
    		
    		
    		//Facing Down
    		case 1:{	
    			if 	((gamebounds[y-1][x] == 0)) 
    				if(gamebounds[y][x-1] == 1) 
    					if ((gamebounds[y][x+1] == 1))	{
    						move[1] = y-1;
    						onlysafemove = true;
    			}
    			else if ((gamebounds[y-1][x] == 1)) 
    				if (gamebounds[y][x-1] == 0) 
    					if ((gamebounds[y][x+1] == 1)) {
    						move[0] = x-1;
    						onlysafemove = true;
    			}
    			else if ((gamebounds[y-1][x] == 1)) 
    				if(gamebounds[y][x-1] == 1) 
    					if ((gamebounds[y][x+1] == 0)) {
    						move[0] = x+1;
    						onlysafemove = true;
    			}
    		}
    		
    		
    		//Facing Left
    		case 2:{	
    			if 	((gamebounds[y+1][x] == 0)) 
    				if (gamebounds[y][x-1] == 1) 
    					if ((gamebounds[y-1][x] == 1))	{
    						move[1] = y+1;
    						onlysafemove = true;
    			}
    			else if ((gamebounds[y+1][x] == 1)) 
    				if (gamebounds[y][x-1] == 0) 
    					if ((gamebounds[y-1][x] == 1)) {
    						move[0] = x-1;
    						onlysafemove = true;
    			}
    			else if ((gamebounds[y+1][x] == 1)) 
    				if (gamebounds[y][x-1] == 1) 
    					if (gamebounds[y-1][x] == 0) {
    						move[1] = y-1;
    						onlysafemove = true;
    			}
    		}
    		
    		
    		//Facing Right
    		case 3:{	
    			if 	((gamebounds[y+1][x] == 0)) 
    				if (gamebounds[y][x+1] == 1) 
    					if ((gamebounds[y-1][x] == 1))	{
    						move[1] = y+1;
    						onlysafemove = true;
    			}
    			else if ((gamebounds[y+1][x] == 1)) 
    				if (gamebounds[y][x+1] == 0) 
    					if ((gamebounds[y-1][x] == 1)) {
    						move[0] = x+1;
    						onlysafemove = true;
    			}
    			else if ((gamebounds[y+1][x] == 1)) 
    				if (gamebounds[y][x+1] == 1) 
    					if((gamebounds[y-1][x] == 0)) {
    						move[1] = y-1;
    						onlysafemove = true;
    			}
    		}
    	}
    	if (onlysafemove) {
    		gamebounds[move[1]][move[0]] = 1;
    		return Arrays.equals(snakemove, move);
    	}
    	else 
    		return false;
    }
    
    
    
    
    private int[] tailattraction(){
    	int[] measure = new int[2]; 
    	int radii = 0;	int i = 0;
    	while (gamebounds[a/2 + radii][b/2 + radii] != 0)	{radii += -2*i;i++;}
    	measure[0] = b/2 + radii; measure[1] = a/2 + radii;
 
    	return measure;
    }
    
    
    
  //makes a detour 
    private int detourmove(int[] livelocation){	
    	int x = livelocation[0]; int y = livelocation[1];
    	boolean[] available = {false, false, false, false};
    	int m = 4;
    	int grossesnumber = 0;
    	if ((y-1 >= 0 )) 
    		if((gamebounds[y-1][x] == 0))	
    			{available[0] = true;}
    	if ((y+1 < a)) 
    		if ((gamebounds[y+1][x] == 0))	
    			{available[1] = true;}
    	if ((x-1 >= 0 )) 
    		if ((gamebounds[y][x-1] == 0))	
    			{available[2] = true;}
    	if ((x+1 < b)) 
    		if ((gamebounds[y][x+1] == 0))	
    			{available[3] = true;}
    	for (int i = 0; i < 4; i++){
    		if (available[i]){
    			int s = 0;
    			switch (i){
    			case 0:{
    				int ny = y-1;	if (ny < 0) break;
    				if ((ny-1 >= 0 )) 
    					if ((gamebounds[ny-1][x] == 0))
    						{s++;}
    		    	if ((x-1 >= 0 )) 
    		    		if ((gamebounds[ny][x-1] == 0))	
    		    			{s++;}
    		    	if ((x+1 < b)) 
    		    		if((gamebounds[ny][x+1] == 0))	
    		    			{s++;}
    			}
    			case 1:{
    				int ny = y+1; if (ny >= a) break;
    		    	if ((ny+1 < a)) 
    		    		if ((gamebounds[ny+1][x] == 0))	
    		    			{s++;}
    		    	if ((x-1 >= 0 )) 
    		    		if ((gamebounds[ny][x-1] == 0))	
    		    			{s++;}
    		    	if ((x+1 < b)) 
    		    		if ((gamebounds[ny][x+1] == 0))	
    		    			{s++;}
    			}
    			case 2:{
    				int nx = x-1;	if (nx < 0) break;
    				if ((y-1 >= 0 )) 
    					if ((gamebounds[y-1][nx] == 0))	
    						{s++;}
    		    	if ((y+1 < a)) 
    		    		if ((gamebounds[y+1][nx] == 0))	
    		    			{s++;}
    		    	if ((nx-1 >= 0 )) 
    		    		if ((gamebounds[y][nx-1] == 0))
    		    			{s++;}
    			}
    			case 3:{
    				int nx = x+1;	if (nx >= b) break;
    				if ((y-1 >= 0 )) 
    					if ((gamebounds[y-1][nx] == 0))	
    						{s++;}
    		    	if ((y+1 < a)) 
    		    		if ((gamebounds[y+1][nx] == 0))	
    		    			{s++;}
    		    	if ((nx+1 < b)) 
    		    		if((gamebounds[y][nx+1] == 0))	
    		    			{s++;}
    			}
    			}
    			if (grossesnumber < s) {
    				grossesnumber = s;
    				m = i;
    			}
    		}
    	}
    	return m;
    }
    

}