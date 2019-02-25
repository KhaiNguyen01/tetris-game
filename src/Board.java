import java.util.Arrays;

// Board.java

/**
 Tao ban choi Tetris, cung cap cac kieu hinh.
 Co chuc nang "undo".
*/
public class Board	{
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	private int max_height;
	private int widths[];  // chieu rong cua cac block da day
	private int heights[]; // chieu dai cua cac block da day

	//bien backup dung de undo
	private boolean[][] gridBackup;
	private int widthsBackup[];
	private int heightsBackup[];
	
	/**
	 Tao ban choi trong voi su cho truoc do cao va rong cua tung block.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;

		widths = new int[height];
		heights = new int[width];

		//tao lap bien backup
		gridBackup = new boolean[width][height];
		widthsBackup = new int[height];
		heightsBackup = new int[width];
	}
	
	/**
	 Tra ve do rong cua block.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Tra ve do cao cua block.
	*/
	public int getHeight() {
		return height;
	}
	
	/**
	 Tra ve gia tri max cua do cao cua cot hien tai. Gia tri bang 0 neu rong.
	*/
	public int getMaxHeight() {	 
		return max_height;
	}
		
	/**
	 Cho mot hinh va toa do x, tra ve gia tri toa do y ve cac diem den cua hinh khi cho no roi thang theo truc x.
	*/
	public int dropHeight(Piece piece, int x) {
		int result = 0;
		int[] skirt = piece.getSkirt();
		for(int i =0 ; i < skirt.length;i++)
		{
			int y = heights[x+i]-skirt[i];
			if(y>result)
				result=y;
		}
		return result;
	}
	/**
	 * Ham tinh chieu cao lon nhat
	 */
	private void recomputeMaxheight()
	{
		max_height = 0;
		for (int i = 0 ; i<heights.length;i++)
		{
			if(max_height<heights[i])
				max_height = heights[i];
		}
	}
	
/*	public void sanityCheck() {

		if (DEBUG) {

			System.out.print(this);
			int[] widthsCheck = new int[height];
			int maxHeightCheck =0;
			for(int i =0; i< width;i++){
				int heightCheck = 0;
				for(int j =0; j< height;j++){
					if(grid[i][j])
					{
						heightCheck = j+1;
						widthsCheck[j]++;

						if(maxHeightCheck<j+1)
							maxHeightCheck = j+1;
					}
				}
				if(heightCheck!=heights[i])
					throw new RuntimeException("Heights check failed");
			}
			if(!Arrays.equals(widthsCheck, widths))
				throw new RuntimeException("Widths check failed");

			if(maxHeightCheck != max_height)
				throw new RuntimeException("Max Height check failed");

		}
	}*/
	
	/**
	 Tra ve do cao cua cot - gia tri toa do y cua block cao nhat + 1. Gia tri bang 0 neu cot khong co block nao.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
	}
	
	/**
	 Tra ve so block trong mot dong cho truoc
	*/
	public int getRowWidth(int y) {
		 return widths[y];
	}
	
	/**
	 Tra ve true neu block trong ban. 
	 Cac block o ben ngoai ma trong vung do rong/cao phu hop luon tra ve gia tri true
	*/
	public boolean getGrid(int x, int y) {
		return (x<0 || y< 0 || x>=width || y >=height || grid[x][y]);
	}
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Tao cac hinh cua tetris. 
	 Cho phep tao cac chuc nang undo de quay lai 1 vi tri truoc do
	*/
	public int place(Piece piece, int x, int y) {
		if (!committed) throw new RuntimeException("place commit problem");
		committed = false;
		backup();
		
		int result = PLACE_OK;
		int pieceX,pieceY;

		TPoint body[] = piece.getBody();
		for(int i =0; i < body.length;i++)
		{
			pieceX = x+body[i].x;
			pieceY = y+body[i].y;

			if(pieceX<0 || pieceY< 0 || pieceX>=width || pieceY >=height )
			{
				result = PLACE_OUT_BOUNDS;
				break;
			}

			if(grid[pieceX][pieceY])
			{
				result = PLACE_BAD;
				break;
			}

			grid[pieceX][pieceY] = true;

			if(heights[pieceX]<pieceY+1)
				heights[pieceX]=pieceY+1;

			widths[pieceY]++;

			if(widths[pieceY] == width)
				result = PLACE_ROW_FILLED;
		}
		recomputeMaxheight();
		//sanityCheck();
		return result;	

	}

	private void backup() {
		System.arraycopy(widths, 0, widthsBackup, 0, widths.length);
		System.arraycopy(heights, 0, heightsBackup, 0, heights.length);
		for(int i =0;i<grid.length;i++)
			System.arraycopy(grid[i], 0, gridBackup[i], 0, grid[i].length);
		
	}

	/**
	 Xoa dong khi no duoc lap day, va chuyen cac dong tren xuong.
	 Tra ve so cac dong con lai
	*/
	public int clearRows() {
		if(committed)
		{
			committed=false;
			backup();
		}

		boolean hasFilledRow = false;
		int rowTo,rowFrom,rowsCleared;
		rowsCleared = 0;

		for(rowTo=0,rowFrom =1;rowFrom<max_height;rowTo++,rowFrom++)
		{
			if(!hasFilledRow && widths[rowTo]==width)
			{
				hasFilledRow=true;
				rowsCleared++;
			}

			while(hasFilledRow && rowFrom<max_height && widths[rowFrom]==width)
			{
				rowsCleared++;
				rowFrom++;
			}

			if(hasFilledRow)
				copySingleRow(rowTo,rowFrom);

		}

		if(hasFilledRow)
			fillEmptyRows(rowTo,max_height);

		for(int i =0;i < heights.length;i++)
		{
			heights[i]-=rowsCleared;
			if(heights[i]>0 && !grid[i][heights[i]-1])
			{
				heights[i]=0;
				for (int j = 0;j<max_height;j++ )
					if(grid[i][j])
						heights[i] = j+1;
			}
		}

		recomputeMaxheight();

		//sanityCheck();
		return rowsCleared;
	}



	private void fillEmptyRows(int lowRow, int highRow) {
		for(int j = lowRow;j<highRow;j++){
			widths[j]=0;
			for(int i = 0;i<width;i++)
				grid[i][j] =false;

		}
	}

	private void copySingleRow(int rowTo, int rowFrom) {
		if(rowFrom<max_height)
		{
			for(int i = 0;i<width;i++)
			{
				grid[i][rowTo] = grid[i][rowFrom];
				widths[rowTo] = widths[rowFrom];
			}
		}
		else
		{
			for(int i = 0;i<width;i++)
			{
				grid[i][rowTo] = false;
				widths[rowTo] = 0;
			}
		}
		
	}

	/**
	 Khoi phuc lai trang thai truoc do 1 vi tri va 1 clearRows().
	 Undo chi duoc goi 1 lan (chi duoc quay lai vi tri truoc do 1 lan)
	*/
	public void undo() {
		if(!committed)
			swap();
		commit();
		//sanityCheck();
	}
	
	
	private void swap(){

		int[] temp = widthsBackup;
		widthsBackup = widths;
		widths = temp;

		temp = heightsBackup;
		heightsBackup = heights;
		heights = temp;

		boolean[][] gridtemp = gridBackup;
		gridBackup = grid;
		grid = gridtemp;

		recomputeMaxheight();
	}

	/**
	 Ghi lai trang thai hien tai nhu la trang thai khoi tao mot lan choi moi.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Render trang thai cho nhu mot xau ky tu dang String. Su dung de in ra trang thai hien tai
	*/
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}
