// Piece.java

import java.util.*;

/**
 Tao hinh

 Piece pyra = new Piece(PYRAMID_STR);		// Tao hinh moi
 int width = pyra.getWidth();			// Lay do rong
 Piece pyra2 = pyramid.computeNextRotation(); // Quay
 
 Piece[] pieces = Piece.getPieces();	// Chuoi hinh goc
 Piece stick = pieces[STICK];
 int width = stick.getWidth();		// Tao do rong
 Piece stick2 = stick.fastRotation();	

*/
public class Piece {
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next;

	static private Piece[] pieces;	

	/**
	 Xac dinh hinh moi cho boi TPoint[]
	*/
	public Piece(TPoint[] points) {
		body = new  TPoint [points.length];
		for (int i = 0 ; i < points.length ; i++)
		{
			body[i] = new TPoint(points[i]);
		}

		computeHeightAndWidth();

		computeSkirt();
	}
	
	private void computeSkirt() {
		skirt = new int[width];
		Arrays.fill(skirt, height-1);
		for (TPoint pt : body)
		{
			if(skirt[pt.x]>pt.y)
				skirt[pt.x]=pt.y;
		}
	}
	
	private void computeHeightAndWidth() {
		int xMax,yMax;
		xMax = yMax =0;

		for (TPoint pt : body)
		{
			if(xMax < pt.x)
				xMax = pt.x;
			if(yMax < pt.y)
				yMax = pt.y;
		}

		width = xMax + 1;
		height = yMax + 1;
	}
	
	public Piece(String points) {
		this(parsePoints(points));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public TPoint[] getBody() {
		return body;
	}

	/**
	 Tra con tro ve quanh cac hinh. 
	*/
	public int[] getSkirt() {
		return skirt;
	}

	/**
	 Tra ve hinh moi duoc quay 90 do so voi chieu dong ho
	 */
	public Piece computeNextRotation() {
		TPoint[] rotatedBody = new TPoint[body.length];
		for (int i = 0 ; i < body.length ; i++)
		{
			rotatedBody[i] = new TPoint(height - body[i].y - 1, body[i].x);
		}

		Piece computedNext = new Piece(rotatedBody);
		return computedNext ;
	}
	
	public Piece fastRotation() {
		return next;
	}
	
	/**
	 Tra ve 2 hinh co cung diem.
	*/
	public boolean equals(Object obj) {
		if (obj == this) return true;
		
		if (!(obj instanceof Piece)) return false;
		Piece other = (Piece)obj;
		if(other.body.length != body.length) return false;
		List<TPoint> myPoints = Arrays.asList(body);
		List<TPoint> otherPoints = Arrays.asList(other.body);

		return myPoints.containsAll(otherPoints);
	}

	// Tao 7 hinh trong tro choi
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0  1 1  1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1	 1 0  2 0";
	public static final String SQUARE_STR	= "0 0	0 1	 1 0  1 1";
	public static final String PYRAMID_STR  = "0 0  1 0  1 1  2 0";
	
	// Gan cac gia tri
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;
	
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {

			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		
		
		return Piece.pieces;
	}
	
	//Quay hinh
	private static Piece makeFastRotations(Piece root) {
		Piece curPiece = new Piece(root.body);
		Piece myRoot = curPiece;
		while (true){
			Piece nextRotation = curPiece.computeNextRotation();

			if(myRoot.equals(nextRotation))
			{
				curPiece.next = myRoot;
				break;
			}
			curPiece.next = nextRotation;
			curPiece = curPiece.next;
		}
		return myRoot;
	}
	
	private static TPoint[] parsePoints(String string) {
		List points = new ArrayList();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}
		
		// Tao mang lua chon
		TPoint[] array = (TPoint[]) points.toArray(new TPoint[0]);
		return array;
	}
}
