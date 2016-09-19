cd build/classes/main

schemagen -cp "." pebl.xsd.Tools
mv schema1.xsd ../../../src/main/resources/schema/tools.xsd

schemagen -cp "." pebl.xsd.Engines
mv schema1.xsd ../../../src/main/resources/schema/engines.xsd

schemagen -cp "." pebl.xsd.Features
mv schema1.xsd ../../../src/main/resources/schema/features.xsd

schemagen -cp "." pebl.xsd.Tests
mv schema1.xsd ../../../src/main/resources/schema/tests.xsd

schemagen -cp "." pebl.xsd.TestResults
mv schema1.xsd ../../../src/main/resources/schema/testresults.xsd

schemagen -cp "." pebl.xsd.PEBL
mv schema1.xsd ../../../src/main/resources/schema/pebl.xsd
