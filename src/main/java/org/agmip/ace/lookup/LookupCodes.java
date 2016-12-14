package org.agmip.ace.lookup;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.agmip.ace.lookup.LookupCodesSingleton;

public class LookupCodes {
  private static final Logger LOG = LoggerFactory.getLogger("LookupCodes.class");

  /**
   * @param variable ICASA variable to look up
   * @param origCode Code value to look up for the variable
   * @param key either common or latin name
   * @param model look up model specific variable
   * @return the value from the lookup table
   */
  public static String lookupCode(String variable, String origCode, String key, String model) {
    LOG.debug("Running lookupCode()");
    String lookupString = "";

    if (model == null) {
      model = "";
    }

    // Standardize to lowercase
    variable    = variable.toLowerCase();
    String code = origCode.toLowerCase();
    key         = key.toLowerCase();
    model       = model.toLowerCase();

    // Standardize the common and latin keys
    if (key.equals("common")) {
      key = "cn";
    }

    if (key.equals("latin")) {
      key = "ln";
    }

    if (! model.equals("")) {
      // Lookup the model specific version first
      String modelString = modelLookupCode(model, variable, code);
      lookupString = variable+"_"+modelString;
    } else {
      lookupString = variable+"_"+code;
    }

    HashMap<String, String> entry = new HashMap<String, String>();
    entry = LookupCodesSingleton.INSTANCE.aceLookup(lookupString);

    LOG.debug("Lookup string: {}", lookupString);
    LOG.debug("Current key:   {}", key);

    if (entry.isEmpty()) {
      return origCode;
    }

    if (entry.containsKey(key)) {
      return entry.get(key);
    } else {
      if (entry.containsKey("cn")) {
        return entry.get("cn");
      } else {
        return origCode;
      }
    }
  }

  /**
   * @param variable ICASA variable to look up
   * @param code Code value to look up for the variable
   * @param origKey either common or latin name
   * @return the value from the lookup table
   */
  public static String lookupCode(String variable, String code, String origKey) {
    return lookupCode(variable, code, origKey, null);
  }

  /**
   * @param model model to look up variable for
   * @param variable ICASA variable to look up
   * @param code Code value to look up for the variable
   * @return the value from the lookup table
   */

  public static String modelLookupCode(String model, String variable, String code) {
    LOG.debug("Running modelLookupCode()");
    model    = model.toLowerCase();
    variable = variable.toLowerCase();
    code     = code.toLowerCase();

    String lookupString = model+"_"+variable+"_"+code;
    return LookupCodesSingleton.INSTANCE.modelLookup(lookupString);
  }
}
