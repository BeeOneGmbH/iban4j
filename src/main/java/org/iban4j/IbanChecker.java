/*
 * Copyright 2014 Michael Greifeneder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iban4j;

import org.iban4j.bban.BbanStructure;
import org.iban4j.bban.BbanStructureEntry;

import java.util.List;

import static org.iban4j.IbanFormatException.Constraint.checksum_only_numeric;
import static org.iban4j.IbanFormatException.Constraint.invalid_length;
import static org.iban4j.IbanFormatException.Constraint.pos_alphanumeric_only;
import static org.iban4j.IbanFormatException.Constraint.pos_numeric_only;
import static org.iban4j.IbanFormatException.Constraint.pos_uppercase_only;
import static org.iban4j.UnsupportedCountryException.Constraint.upper_case;

/** IbanChecker accepts incomplete IBANs
 *
 */
public class IbanChecker {


    private static final int COUNTRY_CODE_INDEX = 0;
    private static final int COUNTRY_CODE_LENGTH = 2;
    private static final int CHECK_DIGIT_INDEX = COUNTRY_CODE_LENGTH;
    private static final int CHECK_DIGIT_LENGTH = 2;
    private static final int BBAN_INDEX = CHECK_DIGIT_INDEX + CHECK_DIGIT_LENGTH;


    /**
     * Throws Exception on error case.
     * @param iban
     * @return true if exact required size, false otherwise
     */
    public static boolean check(String iban) {
        if (iban.length() >= 1) {
            if (!Character.isLetter(iban.charAt(0))) {
                throw new UnsupportedCountryException(upper_case);
            }
            if (iban.length() >= 2) {
                if (!Character.isLetter(iban.charAt(1))) {
                    throw new UnsupportedCountryException(upper_case);
                }
                checkValidCountryCode(iban);
                if (iban.length() >= 3) {
                    if (!Character.isDigit(iban.charAt(2))) {
                        throw new IbanFormatException(checksum_only_numeric);
                    }
                    if (iban.length() >= 4) {
                        if (!Character.isDigit(iban.charAt(3))) {
                            throw new IbanFormatException(checksum_only_numeric);
                        }
                    }
                    if (iban.length() > 4) {
                        int maxLength = getIbanLength(iban);
                        if (iban.length() > maxLength) {
                            throw new IbanFormatException(invalid_length, iban, iban.length(), maxLength);
                        }
                        validateIncompleteBban(iban);
                        if (iban.length() == maxLength) {
                            IbanUtil.validate(iban);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Advanced check for valid BBanStructure to do further checks with BBAN.
     * @param iban
     * @return
     */
    private static void checkValidCountryCode(String iban) {
        IbanUtil.validateCountryCode(iban);
        String cc = IbanUtil.getCountryCode(iban);
        CountryCode countryCode = CountryCode.getByCode(cc);
        BbanStructure structure = BbanStructure.forCountry(countryCode);
    }

    private static void validateIncompleteBban(final String incompleteIban)  {
        CountryCode cc = CountryCode.getByCode(IbanUtil.getCountryCode(incompleteIban));
        BbanStructure bbanStructure = BbanStructure.forCountry(cc);
        String bban = IbanUtil.getBban(incompleteIban);
        List<BbanStructureEntry.EntryCharacterType> list = bbanStructure.generateCharacterTypeList();

        if (bban.length() > list.size()) throw new IbanFormatException(invalid_length, incompleteIban, incompleteIban.length(), list.size() + 4);

        for(int i = 0; i < bban.length(); i++) {
            BbanStructureEntry.EntryCharacterType type = list.get(i);
            if (! validateEntryCharacterType(type, bban.charAt(i))) {
                switch(type) {
                    case a: throw new IbanFormatException(pos_uppercase_only, ""+ (BBAN_INDEX +i));
                    case c: throw new IbanFormatException(pos_alphanumeric_only, "" + (BBAN_INDEX +i));
                    case n: throw new IbanFormatException(pos_numeric_only, "" + (BBAN_INDEX +i));
                }
            }
        }
    }


    private static boolean validateEntryCharacterType(final BbanStructureEntry.EntryCharacterType type, char ch) {
        switch (type) {
            case a: return Character.isUpperCase(ch);
            case c: return Character.isLetterOrDigit(ch);
            case n: return Character.isDigit(ch);
        }
        return false;
    }

    public static int getIbanLength(String iban) {
        CountryCode cc = CountryCode.getByCode(IbanUtil.getCountryCode(iban));
        if (cc == null) return 2;
        BbanStructure bbanStructure = BbanStructure.forCountry(cc);
        return 4 + bbanStructure.getBbanLength();
    }
}

