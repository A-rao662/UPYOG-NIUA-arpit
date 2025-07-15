/*
 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteriorOpenSpaceService extends FeatureProcess {

    // Logger for logging information and errors
    private static Logger LOG = LogManager.getLogger(InteriorOpenSpaceService.class);

    // Constants for rule identifiers and descriptions
    private static final String RULE_43A = "43A";
    private static final String RULE_43 = "43";
    public static final String INTERNALCOURTYARD_DESCRIPTION = "Internal Courtyard";
    public static final String VENTILATIONSHAFT_DESCRIPTION = "Ventilation Shaft";
    public static final String AREA = "Area  ";
    public static final String MINIMUM_AREA = "Minimum area ";
    public static final String MINIMUM_WIDTH = "Minimum width ";
    public static final String AT_FLOOR = " at floor ";

    // Variables to store permissible values for interior open spaces
    public static BigDecimal minInteriorAreaValueOne = BigDecimal.ZERO;
    public static BigDecimal minInteriorAreaValueTwo = BigDecimal.ZERO;
    public static BigDecimal minInteriorWidthValueOne = BigDecimal.ZERO;
    public static BigDecimal minInteriorWidthValueTwo = BigDecimal.ZERO;
    public static BigDecimal minVentilationAreaValueOne = BigDecimal.ZERO;
    public static BigDecimal minVentilationAreaValueTwo = BigDecimal.ZERO;
    public static BigDecimal minVentilationWidthValueOne = BigDecimal.ZERO;
    public static BigDecimal minVentilationWidthValueTwo = BigDecimal.ZERO;

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	CacheManagerMdms cache;
	

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes the given plan to validate interior open spaces.
     * Fetches permissible values for interior courtyards and ventilation shafts and validates them against the plan details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */


    
    @Override
    public Plan process(Plan pl) {
        setMinValuesFromMatchedRule(pl);
        processInteriorOpenSpaces(pl);
        return pl;
    }

    private void setMinValuesFromMatchedRule(Plan pl) {
        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.INTERIOR_OPEN_SPACE_SERVICE, false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream()
            .map(obj -> (MdmsFeatureRule) obj)
            .findFirst();

        if (matchedRule.isPresent()) {
            MdmsFeatureRule rule = matchedRule.get();
            minInteriorAreaValueOne = rule.getMinInteriorAreaValueOne();
            minInteriorAreaValueTwo = rule.getMinInteriorAreaValueTwo();
            minInteriorWidthValueOne = rule.getMinInteriorWidthValueOne();
            minInteriorWidthValueTwo = rule.getMinInteriorWidthValueTwo();
            minVentilationAreaValueOne = rule.getMinVentilationAreaValueOne();
            minVentilationAreaValueTwo = rule.getMinVentilationAreaValueTwo();
            minVentilationWidthValueOne = rule.getMinVentilationWidthValueOne();
            minVentilationWidthValueTwo = rule.getMinVentilationWidthValueOne(); // as in original code
        }
    }

	private void processInteriorOpenSpaces(Plan pl) {
		for (Block b : pl.getBlocks()) {
			ScrutinyDetail scrutinyDetail = createScrutinyDetail();
			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
					&& !b.getBuilding().getFloors().isEmpty()) {
				for (Floor f : b.getBuilding().getFloors()) {
					processOpenSpaceComponent(pl, scrutinyDetail, f,
							f.getInteriorOpenSpace().getVentilationShaft().getMeasurements(),
							minVentilationAreaValueOne, minVentilationAreaValueTwo, minVentilationWidthValueOne,
							minVentilationWidthValueTwo, RULE_43, RULE_43A, VENTILATIONSHAFT_DESCRIPTION);
					processOpenSpaceComponent(pl, scrutinyDetail, f,
							f.getInteriorOpenSpace().getInnerCourtYard().getMeasurements(), minInteriorAreaValueOne,
							minInteriorAreaValueTwo, minInteriorWidthValueOne, minInteriorWidthValueTwo, RULE_43,
							RULE_43A, INTERNALCOURTYARD_DESCRIPTION);
				}
			}
		}
	}

	private void processOpenSpaceComponent(Plan pl, ScrutinyDetail scrutinyDetail, Floor f,
			List<Measurement> measurements, BigDecimal areaValueOne, BigDecimal areaValueTwo, BigDecimal widthValueOne,
			BigDecimal widthValueTwo, String ruleNoArea, String ruleNoWidth, String description) {
		if (measurements != null && !measurements.isEmpty()) {
			BigDecimal minArea = measurements.stream().map(Measurement::getArea).reduce(BigDecimal::min)
					.orElse(BigDecimal.ZERO);
			BigDecimal minWidth = measurements.stream().map(Measurement::getWidth).reduce(BigDecimal::min)
					.orElse(BigDecimal.ZERO);

// Area validation
			if (minArea.compareTo(areaValueOne) > 0) {
				Map<String, String> details = new HashMap<>();
				details.put(RULE_NO, ruleNoArea);
				details.put(DESCRIPTION, description);
				details.put(REQUIRED, MINIMUM_AREA + areaValueTwo.toString() + " Sq. M  ");
				details.put(PROVIDED, AREA + minArea + AT_FLOOR + f.getNumber());
				details.put(STATUS, minArea.compareTo(areaValueTwo) >= 0 ? Result.Accepted.getResultVal()
						: Result.Not_Accepted.getResultVal());

				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}

// Width validation
			if (minWidth.compareTo(widthValueOne) > 0) {
				Map<String, String> details = new HashMap<>();
				details.put(RULE_NO, ruleNoWidth);
				details.put(DESCRIPTION, description);
				details.put(REQUIRED, MINIMUM_WIDTH + widthValueTwo.toString() + " M ");
				details.put(PROVIDED, AREA + minWidth + AT_FLOOR + f.getNumber());
				details.put(STATUS, minWidth.compareTo(widthValueTwo) >= 0 ? Result.Accepted.getResultVal()
						: Result.Not_Accepted.getResultVal());

				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}
	}

    private ScrutinyDetail createScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Interior Open Space");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
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
