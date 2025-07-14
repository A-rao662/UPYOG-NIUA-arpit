/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FireTenderMovement extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(FireTenderMovement.class);

    // Rule identifier for fire tender movement
    private static final String RULE_36_3 = "36-3";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	CacheManagerMdms cache;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param plan The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

	/**
	 * Processes the given plan to validate fire tender movement. Fetches
	 * permissible values for fire tender movement and validates them against the
	 * plan details.
	 *
	 * @param plan The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */
//	@Override
//	public Plan process(Plan plan) {
//		// Map to store validation errors
//		HashMap<String, String> errors = new HashMap<>();
//
//		// Variables to store permissible values for fire tender movement
//		BigDecimal FireTenderMovementValueOne = BigDecimal.ZERO;
//		BigDecimal FireTenderMovementValueTwo = BigDecimal.ZERO;
//
//		// Fetch all rules for the given plan from the cache.
//		// Then, filter to find the first rule where the condition falls within the
//		// defined range.
//		// If a matching rule is found, proceed with its processing.
//
//		List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.FIRE_TENDER_MOVEMENT, false);
//		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
//
//		if (matchedRule.isPresent()) {
//			MdmsFeatureRule rule = matchedRule.get();
//			FireTenderMovementValueOne = rule.getFireTenderMovementValueOne();
//			FireTenderMovementValueTwo = rule.getFireTenderMovementValueTwo();
//		}
//
//		// Iterate through all blocks in the plan
//		for (Block block : plan.getBlocks()) {
//			// Initialize scrutiny details for fire tender movement validation
//			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//			scrutinyDetail.addColumnHeading(1, RULE_NO);
//			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
//			scrutinyDetail.addColumnHeading(4, PROVIDED);
//			scrutinyDetail.addColumnHeading(5, STATUS);
//			scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Fire Tender Movement");
//
//			// Validate fire tender movement for the block
//			if (block.getBuilding() != null && block.getBuilding().getBuildingHeight()
//					.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
//					.compareTo(FireTenderMovementValueOne) > 0) {
//				org.egov.common.entity.edcr.FireTenderMovement fireTenderMovement = block.getFireTenderMovement();
//				if (fireTenderMovement != null) {
//					// Get the minimum width of fire tender movement
//					List<BigDecimal> widths = fireTenderMovement.getFireTenderMovements().stream()
//							.map(fireTenderMovmnt -> fireTenderMovmnt.getWidth()).collect(Collectors.toList());
//					BigDecimal minWidth = widths.stream().reduce(BigDecimal::min).get();
//					BigDecimal providedWidth = minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
//							DcrConstants.ROUNDMODE_MEASUREMENTS);
//					Boolean isAccepted = providedWidth.compareTo(FireTenderMovementValueTwo) >= 0;
//
//					// Add scrutiny details for fire tender movement
//					Map<String, String> details = new HashMap<>();
//					details.put(RULE_NO, RULE_36_3);
//					details.put(DESCRIPTION, "Width of fire tender movement");
//					details.put(PERMISSIBLE, ">= " + FireTenderMovementValueTwo.toString());
//					details.put(PROVIDED, providedWidth.toString());
//					details.put(STATUS,
//							isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
//					scrutinyDetail.getDetail().add(details);
//					plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//
//					// Add errors if fire tender movement is not inside the required setbacks
//					if (!fireTenderMovement.getErrors().isEmpty()) {
//						StringBuffer yardNames = new StringBuffer();
//						for (String yardName : fireTenderMovement.getErrors()) {
//							yardNames = yardNames.append(yardName).append(", ");
//						}
//						errors.put("FTM_SETBACK", "Fire tender movement for block " + block.getNumber()
//								+ " is not inside " + yardNames.toString().substring(0, yardNames.length() - 2) + ".");
//						plan.addErrors(errors);
//					}
//				} else {
//					// Add error if fire tender movement is not defined for the block
//					errors.put("BLK_FTM_" + block.getNumber(),
//							"Fire tender movement not defined for Block " + block.getNumber());
//					plan.addErrors(errors);
//				}
//			}
//		}
//
//		return plan;
//	}
    
    @Override
    public Plan process(Plan plan) {
        HashMap<String, String> errors = new HashMap<>();

        // Fetch permissible values from rules
        List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.FIRE_TENDER_MOVEMENT, false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

        BigDecimal fireTenderValueOne = BigDecimal.ZERO;
        BigDecimal fireTenderValueTwo = BigDecimal.ZERO;

        if (matchedRule.isPresent()) {
            MdmsFeatureRule rule = matchedRule.get();
            fireTenderValueOne = rule.getFireTenderMovementValueOne();
            fireTenderValueTwo = rule.getFireTenderMovementValueTwo();
        }

        for (Block block : plan.getBlocks()) {
            processBlockForFireTender(plan, block, fireTenderValueOne, fireTenderValueTwo, errors);
        }

        return plan;
    }
    
    private void processBlockForFireTender(Plan plan, Block block,
            BigDecimal minRequiredHeight, BigDecimal minRequiredWidth,
            Map<String, String> errors) {

if (block.getBuilding() == null) return;

BigDecimal buildingHeight = block.getBuilding().getBuildingHeight()
.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

if (buildingHeight.compareTo(minRequiredHeight) <= 0) return;

ScrutinyDetail scrutinyDetail = createScrutinyDetail(block.getNumber(), "Fire Tender Movement");

org.egov.common.entity.edcr.FireTenderMovement fireTenderMovement = block.getFireTenderMovement();

if (fireTenderMovement == null) {
errors.put("BLK_FTM_" + block.getNumber(), "Fire tender movement not defined for Block " + block.getNumber());
plan.addErrors(errors);
return;
}

validateFireTenderWidth(plan, block, fireTenderMovement, minRequiredWidth, scrutinyDetail, errors);
}

    
private void validateFireTenderWidth(Plan plan, Block block,
		org.egov.common.entity.edcr.FireTenderMovement fireTenderMovement, BigDecimal minRequiredWidth,
		ScrutinyDetail scrutinyDetail, Map<String, String> errors) {

	List<BigDecimal> widths = fireTenderMovement.getFireTenderMovements().stream().map(ftm -> ftm.getWidth())
			.collect(Collectors.toList());

	if (widths.isEmpty())
		return;

	BigDecimal providedWidth = widths.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO)
			.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

	boolean isAccepted = providedWidth.compareTo(minRequiredWidth) >= 0;

	Map<String, String> details = new HashMap<>();
	details.put(RULE_NO, RULE_36_3);
	details.put(DESCRIPTION, "Width of fire tender movement");
	details.put(PERMISSIBLE, ">= " + minRequiredWidth.toPlainString());
	details.put(PROVIDED, providedWidth.toPlainString());
	details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
	scrutinyDetail.getDetail().add(details);

	plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	if (!fireTenderMovement.getErrors().isEmpty()) {
		String yardNames = String.join(", ", fireTenderMovement.getErrors());
		errors.put("FTM_SETBACK",
				"Fire tender movement for block " + block.getNumber() + " is not inside " + yardNames + ".");
		plan.addErrors(errors);
	}
}

    private ScrutinyDetail createScrutinyDetail(String string, String feature) {
        ScrutinyDetail sd = new ScrutinyDetail();
        sd.setKey("Block_" + string + "_" + feature);
        sd.addColumnHeading(1, RULE_NO);
        sd.addColumnHeading(2, DESCRIPTION);
        sd.addColumnHeading(3, PERMISSIBLE);
        sd.addColumnHeading(4, PROVIDED);
        sd.addColumnHeading(5, STATUS);
        return sd;
    }


    /**
     * Returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}