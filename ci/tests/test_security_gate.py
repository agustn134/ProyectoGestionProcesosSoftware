import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

GATE = Path(__file__).resolve().parents[1]/'security_gate.py'

class SecurityGateTests(unittest.TestCase):
    def invoke(self, findings=(), policy=False, source='class Demo {}', test='<testcase name="contextoSeguroSinSseNiXslt"/>', expires='2099-01-01'):
        with tempfile.TemporaryDirectory() as temp:
            root=Path(temp)
            module=root/'app'
            (module/'src/main/java').mkdir(parents=True)
            (module/'src/main/java/Demo.java').write_text(source)
            (module/'target/surefire-reports').mkdir(parents=True)
            (module/'target/surefire-reports/TEST-context.xml').write_text('<testsuite>'+test+'</testsuite>')
            package={'name':'org.springframework:spring-webmvc','version':'6.2.19'}
            raw={'results':[{'packages':[{'package':package,'vulnerabilities':[{'id':i} for i in findings]}]}]}
            (root/'osv.json').write_text(json.dumps(raw))
            args=[sys.executable,str(GATE),'--report',str(root/'osv.json'),'--output',str(root/'result.json')]
            if policy:
                rules={'expires':expires,'requiredPassingTest':'contextoSeguroSinSseNiXslt','rules':[
                    {'id':'GHSA-j9f9-w8pj-32f8','package':package['name'],'version':package['version'],'reason':'SSE no configurado, comprobación de regresión'}]}
                (root/'policy.json').write_text(json.dumps(rules))
                args+=['--module',str(module),'--context-policy',str(root/'policy.json')]
            result=subprocess.run(args,capture_output=True,text=True)
            output=json.loads((root/'result.json').read_text()) if (root/'result.json').exists() else None
            return result.returncode,output

    def test_clean_inventory_passes_without_exceptions(self):
        code,result=self.invoke()
        self.assertEqual(0,code)
        self.assertEqual('PASS',result['status'])

    def test_unreviewed_finding_fails(self):
        code,result=self.invoke(['GHSA-new'])
        self.assertEqual(1,code)
        self.assertEqual(1,result['blockingFindingCount'])

    def test_context_review_preserves_raw_finding_and_disposition(self):
        code,result=self.invoke(['GHSA-j9f9-w8pj-32f8'],policy=True)
        self.assertEqual(0,code)
        self.assertEqual('PASS_WITH_CONTEXTUAL_EXCEPTIONS',result['status'])
        self.assertEqual(1,result['findingCount'])
        self.assertEqual([],result['suppressedFindings'])
        self.assertEqual('VERSION_AFFECTED_CONDITIONS_NOT_OBSERVED',result['findings'][0]['disposition'])

    def test_new_finding_still_blocks_alongside_reviewed_one(self):
        code,result=self.invoke(['GHSA-j9f9-w8pj-32f8','GHSA-new'],policy=True)
        self.assertEqual(1,code)
        self.assertEqual(2,result['findingCount'])
        self.assertEqual(1,result['blockingFindingCount'])

    def test_skipped_context_cannot_authorize_exception(self):
        code,_=self.invoke(['GHSA-j9f9-w8pj-32f8'],policy=True,test='<testcase name="contextoSeguroSinSseNiXslt"><skipped/></testcase>')
        self.assertNotEqual(0,code)

    def test_introducing_sse_invalidates_context_review(self):
        code,_=self.invoke(['GHSA-j9f9-w8pj-32f8'],policy=True,source='class Demo { SseEmitter events; }')
        self.assertNotEqual(0,code)

    def test_expired_context_review_fails(self):
        code,_=self.invoke(['GHSA-j9f9-w8pj-32f8'],policy=True,expires='2000-01-01')
        self.assertNotEqual(0,code)

    def test_similar_test_name_cannot_authorize_exception(self):
        code,_=self.invoke(['GHSA-j9f9-w8pj-32f8'],policy=True,test='<testcase name="contextoSeguroSinSseNiXsltFake"/>')
        self.assertNotEqual(0,code)

    def test_nested_wildcard_invalidates_context_review(self):
        code,_=self.invoke(['GHSA-j9f9-w8pj-32f8'],policy=True,source='class Demo { String route="/x/**"; }')
        self.assertNotEqual(0,code)

if __name__=='__main__':
    unittest.main()
