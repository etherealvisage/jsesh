/*
 * CompositeHieroglyphDatabase.java
 *
 * Created on 27 sept. 2007, 17:40:08
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsesh.hieroglyphs.data;

import jsesh.hieroglyphs.data.io.SignDescriptionBuilderToHieroglyphDatabaseAdapter;
import jsesh.hieroglyphs.data.io.SignDescriptionReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import jsesh.hieroglyphs.resources.HieroglyphResources;

import jsesh.resources.ResourcesManager;

import org.xml.sax.SAXException;

/**
 * A hieroglyph manager which aggregates information from two sources : one for
 * the standard JSesh, one user-provided.
 * 
 * @author rosmord
 */
public class CompositeHieroglyphDatabase implements HieroglyphDatabaseInterface {

    private static CompositeHieroglyphDatabase instance;

    private ManuelDeCodage basicManuelDeCodageManager = ManuelDeCodage
            .getInstance();

    private SimpleHieroglyphDatabase distributionInfoManager = new SimpleHieroglyphDatabase(
            basicManuelDeCodageManager);

    private SimpleHieroglyphDatabase userInfoManager = new SimpleHieroglyphDatabase(
            basicManuelDeCodageManager);

    private boolean userFileCorrect = true;

    private String userFileMessage = "";
    
    private Map<String,Collection<SignVariant>> variantMap;

    // Now that there is an explicit way to remember possibilities (the
    // repository), remove this variable
    // private Map<String, PossibilitiesList> possibilityListsForKeyBoard = new
    // HashMap<String, PossibilitiesList>();

    public static CompositeHieroglyphDatabase getInstance() {
        if (instance == null) {
            instance = new CompositeHieroglyphDatabase();
        }

        return instance;
    }

    /**
     * Build the default hieroglyphic manager, filled using two different files.
     */
    public CompositeHieroglyphDatabase() {
        try {
            readFiles();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the XML files containing the signs descriptions.
     * 
     * @throws IOException
     * @throws SAXException
     * 
     */
    private void readFiles() throws SAXException, IOException {

        SignDescriptionReader reader = new SignDescriptionReader(
                new SignDescriptionBuilderToHieroglyphDatabaseAdapter(
                        distributionInfoManager));

        // Read "standard" signs description
        InputStream in1 = HieroglyphResources.getSignsDescriptionXML();
        if (in1 != null) {
            reader.readSignDescription(in1);
        } else {
            System.err.println("Sign description file not found.");
        }

        // We don't want errors in the user-defined file to
        // prevent the software from starting
        try {
            // Read user signs descriptions (if any is available)
            reader = new SignDescriptionReader(
                    new SignDescriptionBuilderToHieroglyphDatabaseAdapter(
                            userInfoManager));

            File f = getUserSignDefinitionFile();
            if (f.exists()) {
                // System.err.println("Reading user sign definition");
                InputStream in2 = new FileInputStream(f);
                reader.readSignDescription(in2);
            }
        } catch (Exception e) {
            userFileCorrect = false;
            userFileMessage = e.getMessage();
        }
    }

    /**
     * Returns true if the user sign definition file is correct (or if there is
     * no user sign definition file). This file is the XML file used to define
     * the signs preconditions.
     * 
     * @return
     */
    public boolean isUserFileCorrect() {
        return userFileCorrect;
    }

    /**
     * Return the error message created when failing to read userMessage. This
     * is only meaningful if the user sign definition file is not correct.
     * 
     * @return
     * @see #isUserFileCorrect()
     */
    public String getUserFileMessage() {
        return userFileMessage;
    }

    public static File getUserSignDefinitionFile() {

        File f = ResourcesManager.getInstance().getUserPrefsDirectory();
        f = new File(f, "signs_definition.xml");
        return f;
    }

    public String getCanonicalCode(String code) {
        // standard procedure. If we create a super abstract class, could be in
        // it.
        return distributionInfoManager.getCanonicalCode(code);
    }

    public Collection<String> getCodesForFamily(String familyCode,
            boolean userCodes) {
        TreeSet<String> s = new TreeSet<String>(
                GardinerCode.getCodeComparator());
        s.addAll(distributionInfoManager.getCodesForFamily(familyCode,
                userCodes));
        s.addAll(userInfoManager.getCodesForFamily(familyCode, userCodes));
        return s;
    }

    public Set<String> getCodesSet() {
        TreeSet<String> s = new TreeSet<String>(
                GardinerCode.getCodeComparator());
        s.addAll(distributionInfoManager.getCodesSet());
        s.addAll(userInfoManager.getCodesSet());
        return s;
    }

    public String getDescriptionFor(String code) {
        String result = userInfoManager.getDescriptionFor(code);
        if ("".equals(result)) {
            result = distributionInfoManager.getDescriptionFor(code);
        }
        return result;
    }

    // TODO move this method, as it depends only on the Manuel structure, not
    // content.
    public List<HieroglyphFamily> getFamilies() {
        return distributionInfoManager.getFamilies();
    }

    // Return a possibility list built from both (NOTE TO SELF: KEEP THE
    // POSSIBILITY LISTS...)
    // TODO: separate the database aspect, which is stateless, from
    // the dynamic aspect (keeping track of possibilitylists, mainly).
    public PossibilitiesList getPossibilityFor(String phoneticValue,
            String level) {
        PossibilitiesList result = null;
        if (SignDescriptionConstants.KEYBOARD.equals(level)) {
            result = fetchPossibilityListFromBases(phoneticValue, level);
        } else
            result = fetchPossibilityListFromBases(phoneticValue, level);

        return result;

    }

    private PossibilitiesList fetchPossibilityListFromBases(
            String phoneticValue, String level) {
        PossibilitiesList p1 = distributionInfoManager.getPossibilityFor(
                phoneticValue, level);
        PossibilitiesList p2 = userInfoManager.getPossibilityFor(phoneticValue,
                level);
        return p1.add(p2);
    }

    public Collection<String> getSignsContaining(String code) {
        TreeSet<String> result = new TreeSet<String>(
                GardinerCode.getCodeComparator());
        result.addAll(distributionInfoManager.getSignsContaining(code));
        result.addAll(userInfoManager.getSignsContaining(code));
        return result;
    }

    public Collection<String> getSignsIn(String code) {
        TreeSet<String> result = new TreeSet<String>(
                GardinerCode.getCodeComparator());
        result.addAll(distributionInfoManager.getSignsIn(code));
        result.addAll(userInfoManager.getSignsIn(code));
        return result;
    }

    public Collection<String> getSignsWithTagInFamily(String currentTag,
            String familyName) {
        TreeSet<String> result = new TreeSet<String>(
                GardinerCode.getCodeComparator());
        result.addAll(distributionInfoManager.getSignsWithTagInFamily(
                currentTag, familyName));
        result.addAll(userInfoManager.getSignsWithTagInFamily(currentTag,
                familyName));
        return result;
    }

    public Collection<String> getSignsWithoutTagInFamily(String familyName) {
        TreeSet<String> result = new TreeSet<String>(
                GardinerCode.getCodeComparator());
        result.addAll(distributionInfoManager
                .getSignsWithoutTagInFamily(familyName));
        result.addAll(userInfoManager.getSignsWithoutTagInFamily(familyName));
        return result;
    }

    public Collection<String> getTagsForFamily(String familyName) {
        TreeSet<String> result = new TreeSet<String>();
        result.addAll(distributionInfoManager.getTagsForFamily(familyName));
        result.addAll(userInfoManager.getTagsForFamily(familyName));
        return result;
    }

    public Collection<String> getTagsForSign(String gardinerCode) {
        TreeSet<String> result = new TreeSet<String>();
        result.addAll(distributionInfoManager.getTagsForSign(gardinerCode));
        result.addAll(userInfoManager.getTagsForSign(gardinerCode));
        return result;
    }

    public List<String> getValuesFor(String gardinerCode) {
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(distributionInfoManager.getValuesFor(gardinerCode));
        result.addAll(userInfoManager.getValuesFor(gardinerCode));
        return result;
    }

    @Override
    public Collection<SignVariant> getVariants(String code) {
        TreeSet<SignVariant> result = new TreeSet<>();
        result.addAll(distributionInfoManager.getVariants(code));
        result.addAll(userInfoManager.getVariants(code));
        return result;
    }

    @Override
    public Collection<String> getVariants(String code, VariantTypeForSearches variantTypeForSearches) {
        Collection<SignVariant> variants = getCachedVariantMap().getOrDefault(code, Collections.emptySet());  
        return variants.stream()
                .filter(info -> variantTypeForSearches.match(info.getType()))
                .map(info -> info.getCode())
                .collect(Collectors.toSet());
    }

    
    /**
     * Caching system for variant map...
     * @return 
     */
    private Map<String, Collection<SignVariant>> getCachedVariantMap() {
        if (variantMap == null) {
            variantMap = new HashMap<>();
            for (String code: distributionInfoManager.getCodesSet()) {
                for (SignVariant v: distributionInfoManager.getVariants(code)) {
                    addVariant(code, v.getCode(), v.getType());
                }
            }
            for (String code: userInfoManager.getCodesSet()) {
                // Can refer to distributionInfoManager !
                for (SignVariant v: userInfoManager.getVariants(code)) {
                    addVariant(code, v.getCode(), v.getType());
                }
            }
        }
        return variantMap;
    }
    
    /**
     * Record in cache a variant relationship between two signs.
     * @param code1
     * @param code2
     * @param type
     */
    private void addVariant(String code1, String code2, SignVariantType type) {
        if (! variantMap.containsKey(code1)) {
            variantMap.put(code1, new HashSet<>());
        }
        if (! variantMap.containsKey(code2)) {
            variantMap.put(code2, new HashSet<>());
        }
        variantMap.get(code1).add(new SignVariant(code2, type));
        variantMap.get(code2).add(new SignVariant(code1, type));
    }
    

    @Override
    public PossibilitiesList getCodesStartingWith(String code) {
        PossibilitiesList p = distributionInfoManager
                .getCodesStartingWith(code);
        PossibilitiesList p1 = userInfoManager.getCodesStartingWith(code);
        return p.add(p1);
    }

    @Override
    public boolean isAlwaysDisplayed(String code) {
        return distributionInfoManager.isAlwaysDisplayed(code)
                || userInfoManager.isAlwaysDisplayed(code);
    }

    /**
     * Get the all the codes which might match a "generic gardiner code".
     * 
     * 
     * fact, there are two conceptual layers of signs : Glyphs and Characters,
     * roughly. Characters are described (more or less consistently, see for
     * example Y1 and Y2 as a counter example) by Gardiner code. Those codes
     * might correspond to user defined glyphs (US1A1 for A1, as an example).
     * Notice that this is somehow ad-hoc, as some user codes are really
     * character codes (signs ending in XT, for instance, are supposed to be
     * real new signs, not variants of existing signs, so US1A1XT would be
     * completely different from US2A1XT).
     * 
     * So this method is not about signs values or equivalence, but mostly an
     * input/output facility.
     * 
     * @param code
     * @return
     */
    public PossibilitiesList getSuitableSignsForCode(String code) {
        if (GardinerCode.isCorrectGardinerCodeIgnoreCase(code)) {

            PossibilitiesList p = distributionInfoManager
                    .getSuitableSignsForCode(code);
            PossibilitiesList p1 = userInfoManager
                    .getSuitableSignsForCode(code);
            return p.add(p1);
        } else {
            return new PossibilitiesList(code);
        }
    }

}