package org.agmip.ace;

import java.io.IOException;

/**
 * Interface for all base component types
 */
public interface IAceBaseComponent {
    /**
     * Retrieve the SHA-256 hash code for this component.
     * @param forceRegenerate if the SHA-256 hash code is to regenerated.
     * @return the SHA-256 hash code for this component.
     * @throws IOException if there is an I/O error
     */
    public String getId(boolean forceRegenerate) throws IOException;
    /**
     * Generate the SHA-256 hash code for this component.
     * @return the SHA-256 hash code for this component.
     * @throws IOException if there is an I/O error
     */
    public String generateId() throws IOException;
    /**
     * Validate the SHA-256 hash code for this component.
     * @return if the SHA-256 hash code is correct for this component.
     * @throws IOException if there is an I/O error
     */
    public boolean validId() throws IOException;
    /**
     * Return the {@link AceComponentType} for this component.
     * @return the AceComponentType for this component.
     */
    public AceComponentType getComponentType();
    /**
     * Return the raw {@code byte[]} JSON for this component.
     * @return JSON as a {@code byte[]}
     * @throws IOException if there is an I/O error
     */
    public byte[] getRawComponent() throws IOException;
    /**
     * Return the reassembled {@code byte[]} JSON for this component.
     * <p>Part of the process of generating a component is to break down
     * each component into subcomponents. This method reads each subcomponent
     * and into a single {@code byte[]} along with this primary component data.
     * This will resemble the initial input given to the component.</p>
     * <p><strong>NOTE:</strong> this should be called during the process of
     * regenerating the ID for this component, since the ID is based on all
     * subcomponents as well.</p>
     * @return nested JSON structure with this component as the root.
     * @throws IOException if there is an I/O error
     */
    public byte[] rebuildComponent() throws IOException;
}
