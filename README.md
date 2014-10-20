iban4j [![Build Status](https://api.travis-ci.org/repositories/arturmkrtchyan/iban4j.png)](https://travis-ci.org/arturmkrtchyan/iban4j)
======

A Java library for generation and validation of the International Bank Account Numbers (<a href="http://en.wikipedia.org/wiki/ISO_13616" target="_blank">IBAN ISO_13616</a>) and Business Identifier Codes (<a href="http://en.wikipedia.org/wiki/ISO_9362" target="_blank">BIC ISO_9362</a>).


#### Iban quick examples:

```java
 // How to generate Iban
 Iban iban = new Iban.Builder()
                .countryCode(CountryCode.AT)
                .bankCode("19043")
                .accountNumber("00234573201")
                .build();


 // How to create Iban object from String
 Iban iban = Iban.valueOf("DE89370400440532013000");


 // How to validate Iban 
 try {
     IbanUtil.validate("AT611904300234573201");
     // valid
 } catch (IbanFormatException e) {
     // invalid
 } catch (InvalidCheckDigitException e) {
     // invalid
 } catch (UnsupportedCountryException e) {
     // invalid
 }
```

#### Bic quick examples:

```java
 //How to create Bic object from String
 Bic bic = Bic.valueOf("DEUTDEFF");


 //How to validate Bic
 try {
     BicUtil.validate("DEUTDEFF500");
     // valid
 } catch (BicFormatException e) {
     // invalid
 }
```

#### Maven dependency: 
```
<dependency>
  <groupId>org.iban4j</groupId>
  <artifactId>iban4j</artifactId>
  <version>2.2.1</version>
</dependency>
```

#### References

- http://en.wikipedia.org/wiki/ISO_13616
- http://en.wikipedia.org/wiki/ISO_9362
- http://www.swift.com/dsp/resources/documents/IBAN_Registry.pdf

## License
Copyright 2014 Artur Mkrtchyan.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
