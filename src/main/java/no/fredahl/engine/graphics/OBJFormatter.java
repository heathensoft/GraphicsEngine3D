package no.fredahl.engine.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary. Using Assimp later.
 *
 * This uses Short as indices primitive. So any .obj models you load has the
 * limit of max indices: Short.MAX_VALUE = 32767. So keep this in mind.
 * It won't crash but very detailed geometry will get fragmented like a rusted old shed.
 *
 * @author Frederik Dahl
 * 25/11/2021
 */


public class OBJFormatter {
    
    public static final class Geometry {
        
        public float[] positions;
        public float[] texCoords;
        public float[] normals;
        public short[] indices;
        
        public short[] toTexels(int w, int h) {
            short[] result = new short[texCoords.length / 2];
            for (int i = 0; i < result.length; i++) {
                float u = texCoords[i];
                float v = texCoords[i+1];
                result[i] = (short) (Math.floor(w*u) + Math.floor(h*v));
            }
            return result;
        }
        
    }
    
    public static Geometry process(List<String> lines) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
    
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v": // Geometric vertex
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt": // Texture coordinate
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn": // Vertex normal
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    break;
                case "f":
                    faces.add(new Face(tokens[1], tokens[2], tokens[3]));
                    break;
                default: // Ignore other lines
                    break;
            }
        }
        return reorderLists(vertices, textures, normals, faces);
    }
    
    private static Geometry reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList,
                                         List<Vector3f> normList, List<Face> facesList) {
    
        Geometry data = new Geometry();
        List<Short> indices = new ArrayList<>();
        // Create position array in the order it has been declared
        data.positions = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            data.positions[i * 3] = pos.x;
            data.positions[i * 3 + 1] = pos.y;
            data.positions[i * 3 + 2] = pos.z;
            i++;
        }
        i = 0;
        data.texCoords = new float[posList.size() * 2];
        data.normals = new float[posList.size() * 3];
        
        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, data.texCoords, data.normals);
            }
        }
        data.indices = new short[indices.size()];
        for (Short idx : indices) data.indices[i++] = idx;
        return data;
    }
    
    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
                                          List<Vector3f> normList, List<Short> indicesList,
                                          float[] texCoordArr, float[] normArr) {
        // Set index for vertex coordinates
        short posIndex = indices.idxPos;
        indicesList.add(posIndex);
        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }
    
    protected static class Face {
        
        private final IdxGroup[] idxGroups = new IdxGroup[3];
        
        public Face(String v1, String v2, String v3) {
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }
        
        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();
            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = (short) (Integer.parseInt(lineTokens[0]) - 1);
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = (short) (textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE);
                if (length > 2) {
                    idxGroup.idxVecNormal = (short) (Integer.parseInt(lineTokens[2]) - 1);
                }
            }
            return idxGroup;
        }
        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }
    
    protected static class IdxGroup {
        
        public static final short NO_VALUE = -1;
        
        public short idxPos;
        
        public short idxTextCoord;
        
        public short idxVecNormal;
        
        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
