package org.rowan.reverie.md2;

import java.io.*;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.reverie.md2.util.LittleEndianInputStream;
import org.rowan.reverie.md2.*;

/**
 * The <code>MD2Loader</code> class provides a singleton object for loading
 * <code>MD2Model</code>s from .md2 files. To avoid unnecessary I/O, MD2 models
 * should only be loaded with this class once, and subsequently coppied as needed.
 * This can be accomplished by passing an <code>MD2Model</code> to the constructor
 * or by calling the model's <code>copy()</code> method.
 *
 * @see org.rowan.reverie.md2.MD2Model
 *
 * @see <a href="http://en.wikipedia.org/wiki/MD2_(file_format)">MD2 File Format</a>
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class MD2Loader {
    
    /*********************************************
     * MARK: Singleton
     *********************************************/
    
    /** Singleton instance. **/
    private static final MD2Loader INSTANCE = new MD2Loader();
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The filepath of the MD2 model being loaded. */
    private String filepath;
    
    /** Input stream used to read the MD2 file. */
    private LittleEndianInputStream input;
    
    /** The MD2 file header data. */
    private Header header;
    
    /** Array of texture paths read from file. */
    private String[] texPaths;
    
    /** Array of texture coordinate pairs read from file. */
    private Vector2D[] texCoords;
    
    /** Array of vertex indices read from file. */
    private int[] vertIndices;
    
    /** Array of texture coordinate indices read from file. */
    private int[] texCoordIndices;
    
    /** Array of frames read from file. */
    private KeyFrame[] frames;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Constructor.
     */
    private MD2Loader() {}
    
    /**
     * Returns the singleton MD2Loader.
     * @return  The singleton MD2Loader.
     */
    public static MD2Loader getInstance() {
        return INSTANCE;
    }
    
    
    /*********************************************
     * MARK: Load
     *********************************************/
    
    /**
     * Loads an MD2 model and all associated animations from a specified MD2 file. 
     * @param filepath  The filepath of the MD2 model file.
     * @return          An <code>MD2Model</code> object representing the model
     *                  defined by the specified file.
     */
    public MD2Model load(String filepath) {
        return load(filepath, Integer.MAX_VALUE);
    }
    
    /**
     * Loads an MD2 model from a specified MD2 file including animation frames
     * up to a given amount. 
     * @param filepath  The filepath of the MD2 model file.
     * @param maxFrames The maximum number of animation frames to load. If the
     *                  given value is less than or equal to 1, a single frame 
     *                  will be loaded.
     * @return          An <code>MD2Model</code> object representing the model
     *                  defined by the specified file.
     */
    public MD2Model load(String filepath, int maxFrames) {
        // store filepath
        this.filepath = filepath;
        
        // get file from path as stream 
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(filepath);
        
        // file not found?
        if (is == null) {
            System.err.println("File not found: " + filepath);
            return null;
        }
        
        // wrap input stream as Little Endian input stream
        this.input = new LittleEndianInputStream(new BufferedInputStream(is));
        
        
        try {
            // mark the start of the input stream
            input.mark(Integer.MAX_VALUE);
            
            // parse file header
            parseHeader();
            
            // parse texture paths
            parseTexPaths();
            
            // parse texture coords
            parseTexCoords();
            
            // parse triangles
            parseTriangles();
            
            // parse frames
            parseFrames(maxFrames);
            
            // close input stream
            input.close();
            
        } catch (Exception e) {
            System.err.println("Failed to parse file: \n" + e);
            return null;
        }
        
        
        // create and return MD2 model
        return new MD2Model(this.texPaths, 
                            this.vertIndices, 
                            this.texCoordIndices, 
                            this.texCoords, 
                            this.frames);
    }
    
    
    /*********************************************
     * MARK: Parsing
     *********************************************/
    
    /**
     * Parses the MD2 file header.
     */
    private void parseHeader() throws Exception {
        // initialize header
        this.header = new Header();
        
        // parse id and verision
        header.id = input.readInt(); 
        header.version = input.readInt();
        
        // verify file header
        if (header.id != 844121161 || header.version != 8)
            throw new Exception("Invalid MD2 File: " + this.filepath);
        
        // parse remaining header data
        header.skinWidth = input.readInt();
        header.skinHeight = input.readInt();
        header.frameSize = input.readInt();
        header.numSkins = input.readInt();
        header.numVerts = input.readInt();
        header.numTexCoord = input.readInt();
        header.numTriangles = input.readInt();
        header.numGLCommands = input.readInt();
        header.numFrames = input.readInt();
        header.offsetSkins = input.readInt();
        header.offsetTexCoord = input.readInt();
        header.offsetTriangles = input.readInt();
        header.offsetFrames = input.readInt();
        header.offsetGLCommands = input.readInt();
        header.offsetEnd = input.readInt();
    }
    
    /**
     * Parses texture paths from the MD2 file.
     */
    private void parseTexPaths() throws Exception {
        // derive directory path
        String dirPath = filepath.replaceAll("[^/]+$", "");
        
        // initialize texture path array
        this.texPaths = new String[header.numSkins];
        
        // reset input stream and skip to desired position
        input.reset();
        input.skip(header.offsetSkins);
        
        for (int i=0; i<header.numSkins; i++) {
            // read texture path (64 bytes)
            String texPath = input.readString(64);
            
            // append directory path and trim whitespace
            texPath = (dirPath + texPath).trim();
            
            // store formatted texture path
            texPaths[i] = texPath;
        }
    }
    
    /**
     * Parses texture coordinates from the MD2 file.
     */
    private void parseTexCoords() throws Exception {
        // initialize texture coord array
        this.texCoords = new Vector2D[header.numTexCoord];
        
        // reset input stream and skip to desired position
        input.reset();
        input.skip(header.offsetTexCoord);
        
        // read, compute, and store texture coordinates
        for (int i=0; i<header.numTexCoord; i++) {
            float u = (float)input.readShort() / (float)header.skinWidth;
            float v = (float)input.readShort() / (float)header.skinHeight;
            texCoords[i] = new Vector2D(u, v);
        }
    }
    
    /**
     * Parses trinagular faces, defined by sets of vertex and texture coordinate
     * indices.
     */
    private void parseTriangles() throws Exception {
        // initialize index arrays
        this.vertIndices = new int[header.numTriangles*3];
        this.texCoordIndices = new int[header.numTriangles*3];
        
        // reset input stream and skip to desired position
        input.reset();
        input.skip(header.offsetTriangles);
        
        for (int i=0; i<header.numTriangles; i++) {
            vertIndices[i*3+2] = input.readUnsignedShort();
            vertIndices[i*3+1] = input.readUnsignedShort();
            vertIndices[i*3+0] = input.readUnsignedShort();
            texCoordIndices[i*3+2] = input.readUnsignedShort();
            texCoordIndices[i*3+1] = input.readUnsignedShort();
            texCoordIndices[i*3+0] = input.readUnsignedShort();
        }
    }
    
    /**
     * Parses up to a specified amount of animation key frames from the MD2 file.
     * @param maxFrames The maximum number of key frames to parse.
     */
    private void parseFrames(int maxFrames) throws Exception {
        // determine the number of frames to load
        int frameCount = Math.min(header.numFrames, maxFrames);
        frameCount = (frameCount < 1)? 1 : frameCount;
        
        // initialize frame array
        this.frames = new KeyFrame[frameCount];
        
        // reset input stream and skip to desired position
        input.reset();
        input.skip(header.offsetFrames);
        
        for (int i=0; i<frameCount; i++) {
            // parse scale
            float scaleX = input.readFloat();
            float scaleY = input.readFloat();
            float scaleZ = input.readFloat();
            
            // parse translate
            float transX = input.readFloat();
            float transY = input.readFloat();
            float transZ = input.readFloat();
            
            // parse name
            String name = input.readString(16);
            
            // initialize vertex array
            Vector3D[] vertices = new Vector3D[header.numVerts];

            // parse and process vertices
            for (int j=0; j<header.numVerts; j++) {
                // read coord data
                float x = (float) input.readUnsignedByte();
                float y = (float) input.readUnsignedByte();
                float z = (float) input.readUnsignedByte();
                
                // "decompress" vertex coords
                x = x * scaleX + transX;
                y = y * scaleY + transY; 
                z = z * scaleZ + transZ;
                
                // construct and store vector
                vertices[j] = new Vector3D(x, y, z);
                                
                // skip normal index (will calculate manually)
                input.readUnsignedByte();
            }
            
            Vector3D[] normals = computeFrameNormals(vertices);
            
            // create and store KeyFrame object
            frames[i] = new KeyFrame(name, vertices, normals);
        }
    }
    
    
    /*********************************************
     * MARK: Helper Methods
     *********************************************/
    
    /**
     * Computes and constructs an array of surface normals for a frame, given
     * a specified array of vertices.
     * @param vertices      The vertices used to draw the frame.
     * @return              An array containing the computed surface normals.
     */
    private Vector3D[] computeFrameNormals(Vector3D[] vertices) {
        // create the surface normal array
        Vector3D[] normals = new Vector3D[header.numTriangles];

        // compute and set normals for each face
        for (int i=0; i<header.numTriangles; i++)
            normals[i] = computeFaceNormal(vertices[this.vertIndices[i*3+0]],
                                           vertices[this.vertIndices[i*3+1]],
                                           vertices[this.vertIndices[i*3+2]]);
            
        // return surface normals
        return normals;
    }
    
    /**
     * Computes the normal vector for a triangularface defined by three given 
     * vertices.
     * @param v1 The first vertex of the surface.
     * @param v2 The second vertex of the surface.
     * @param v3 The third vertex of the surface.
     */
    private Vector3D computeFaceNormal(Vector3D v1, Vector3D v2, Vector3D v3) {
        Vector3D a = v2.subtract(v1);
        Vector3D b = v3.subtract(v1);
        return a.cross(b).unitVector();
    }
    
    
    /*********************************************
     * MARK: Header Class
     *********************************************/
    
    private class Header {
        public int id;                 // file identifier, "IDP2" (844121161) 
        public int version;            // version number (8)
        public int skinWidth;
        public int skinHeight;
        public int frameSize;
        
        public int numSkins;
        public int numVerts;
        public int numTexCoord;
        public int numTriangles;
        public int numGLCommands;
        public int numFrames;
        
        public int offsetSkins;
        public int offsetTexCoord;
        public int offsetTriangles;
        public int offsetFrames;
        public int offsetGLCommands;
        public int offsetEnd;
    }
}
