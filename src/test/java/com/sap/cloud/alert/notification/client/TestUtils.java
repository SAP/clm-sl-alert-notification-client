package com.sap.cloud.alert.notification.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.alert.notification.client.internal.SimpleRetryPolicy;
import com.sap.cloud.alert.notification.client.model.configuration.*;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.sap.cloud.alert.notification.client.model.configuration.Predicate.CONTAINS;
import static com.sap.cloud.alert.notification.client.model.configuration.State.ENABLED;
import static java.lang.Boolean.FALSE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_OK;

public class TestUtils {

    public static final Integer TEST_PAGE = 3;
    public static final Long TEST_TIMESTAMP = 1L;
    public static final String TEST_ID = "TEST_ID";
    public static final State TEST_STATE = ENABLED;
    public static final Long TEST_TOTAL_PAGES = 12L;
    public static final Integer TEST_PAGE_SIZE = 131;
    public static final Integer TEST_DISCARD_AFTER = 4;
    public static final Integer TEST_FALLBACK_TIME = 2;
    public static final String TEST_NAME = "TEST_NAME";
    public static final String TEST_ACTION_NAME = "TEST_ACTION_NAME";
    public static final String TEST_CONDITION_NAME = "TEST_CONDITION_NAME";
    public static final String TEST_SUBSCRIPTION_NAME = "TEST_SUBSCRIPTION_NAME";
    public static final String TEST_TYPE = "TEST_TYPE";
    public static final Boolean TEST_MANDATORY = FALSE;
    public static final Integer TEST_HTTP_ERROR_CODE = 404;
    public static final Predicate TEST_PREDICATE = CONTAINS;
    public static final Long TEST_TOTAL_RESULTS_COUNT = 16L;
    public static final String TEST_MESSAGE = "TEST_MESSAGE";
    public static final Long TEST_TIME_CREATED = 1586082414L;
    public static final Long TEST_LAST_MODIFIED = 1586082415L;
    public static final String TEST_USERNAME = "TEST_USERNAME";
    public static final String TEST_PASSWORD = "TEST_PASSWORD";
    public static final String TEST_PROPERTY_KEY = "eventType";
    public static final String TEST_DESTINATION_NAME = "TEST_DESTINATION_NAME";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final String TEST_KEYSTORE_CONTENT = "TEST_CONTENT";
    public static final String TEST_KEYSTORE_PASSWORD = "test";
    public static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 1);
    public static final String TEST_TEST_OAUTH_RESPONSE = "{\"expires_in\":11342,\"access_token\":\"TEST_ACCESS_TOKEM\"}";
    public static final Set<String> TEST_LABELS = singleton("TEST_LABEL");
    public static final String TEST_PROPERTY_VALUE = "TEST_PROPERTY_VALUE";
    public static final Set<String> TEST_ACTIONS = singleton("TEST_ACTION");
    public static final String TEST_FALLBACK_ACTION = "TEST_FALLBACK_ACTION";
    public static final String TEST_KEYSTORE_CONTENT_PEM = "LS0tLS1CRUdJTiBFTkNSWVBURUQgUFJJVkFURSBLRVktLS0tLQpNSUlKbnpCSkJna3Foa2lHOXcwQkJRMHdQREFiQmdrcWhraUc5dzBCQlF3d0RnUUlqajRsWS9oZ2NCRUNBZ2dBCk1CMEdDV0NHU0FGbEF3UUJLZ1FRaERuSnNrdlNKbGpoM29UbE9CUlZFd1NDQ1ZEME4xVWt5OEJZOXI4b2VNQ24KQVZFSjBzRmQzc0RVSVRVUktmTUZtK1hrSmhOL1E1VVl1WlU1dnJKVnE1dmo4T1BJMHNzL0RsMEI2NWtobGNVYQpwUzBoSlNsSklRbk1uTnN4WFVLdHMwbkVFZGhZakJlSDBZNlB4MGZJKzdQT0xJNWxYUCtNYUMybzJqVHBCM1crCnp0YUxOaDJlTnJLYXpkQnpxbFVneWFLNlJpczJGRUVtVERrUkh2WFVmZnVUWDI1MTVLeldkVHZibyszU2drOXAKa1hkajBKTURvNVZNdmhVQ01Mb2IxSDNyWlM5a1V5Ny9oZVJ4d0FTMkJQYXliRWs2bzVmOVl0dnNOWUZZTFhDYwpFR0t0dzlVREI0L3lVL1NQNDlOL0pNQll1VG5JMzlqRWgxS2UxNFZkMnlEZWxiOW92VHJhemhYK3kyMko1U2dSCmd6ZDdBWkc3NjE2dW5mejdjSkVva3cwcHUzZEQ4Y0pCbnZmWjdnTE04SG56dk0vbnpFNG0rZS9qOFRJeHM3bzgKYUMvMnpYRno3RUNvOEt0UUxVRThtS2EwbHJOZy9pVk5XVXNUUVlKUEJwTTZDMm1wTzByVGJtcjhVOHNPam1CaAp5OXdNcFF2ZEwwT214Z0w2d2g2U3BXOWtLTTAxb3VFZkd1WW82NXZ4Rk9kdmthSzE2NHJxVkQrcnN3ai9vSG5NClJ1SmFFaGlCOEVjZGhzZjZXemIvbzY3YktVR1RQaEwvenB2WEJVYWd1dFE3RzZURzZackJneDlUSzNVenR0YUMKL28xc3dBVmZUbEdoUnQvT08yWnN0OGNrMGJ6QmdzcFo0bFBWTXVHc1VycEpLQzBLK2hCd0ZVeENmVTF5SmZIUApEMFIwbHJTbHQ4MjdZZ0NUbzk3dUJIR2picmFyTDNTb1R2MmZ0bU45cmV5bGVST240UlRZck1PeUVNUzQ4OElsCkV0M1RQNm5XVDBTV3ltc1hub045YlRmR1UyNzJxZVZ1WVRWQ05kUzJFa3h6TzZ1cmc1Y1NRWmVZbkhvbUIyUXMKa3FSTTExa29wek82dGp1VGtRa1RkdldpWGJPcmxCZ0M4UVFoUnJZTWdvMGFSbVNqbURKZnhRNmpwREhFL3RuWApkOG5ieHpLMStnSSsyNThaNGRoK2xpeEpTMmdoYmhpU1dXcmQ4SS85bXd6UE1TMFEwT3g0N0JvalJyRm04Y0tVClZsRVJDaHdqbFlIQWhIanM5VVgvMmZid3lxSWt0VkhrUjNXYStzdisvZE1aTDJpcW5nb01veTQ3RVRYZUlhc2MKK0VMNkZoN0dqdjZaQVdxWW1GOUVoTVRUVmZVakVlMTNOVm1qUUxQMkhYN0p2aExzbWFpcHUrZk45Mlp6eTRqSApRRnR4VnRMUkhhYnp5cE53N3pMb0twbk43Tk9ma2ZtdTlqWFFEdDloc1N4TGd6NHJPWkVJYTZZU3orOHBjcHFGCkR2TVoyTFB5VkRseGxzTFNyN29qWHNiNi9aU2hveGhBTk9SdlhZbUQvUGVpRnhsWFJEVUxrNVJoQk9sS0ZHQWoKRC9PaU5YSE5mQ1ZtcElQWWpXb3lmRjl3Y3V1RytZbFFhWFhpUExaK3RzbVU0bUR5cldRQWV6Zi9VUVB6blRJVApUbHhma3hURHdnb3BjU2dzMkgwVFhGYjFRN1lTNXlXUVMrUjJ3aGM2YS9XMkFPamNrOEJsaGZMQlhjTE1rWDdaCm0rZHk0djNGOWtDVGNMb1o1MEhoaXpVWUJBZDJ0VVBGREQwVC85WGtNbHVHbkhTMWhMR3BVU29WUnU2cFlVMGEKSlU0dXJrMjUxRTcxbE1LdEhMaFBkeHY3NVp2aGZIbnQ0eGZENWhTQW1DRENoZ1VpL3lCSnN4bUNlcE1SN3N5VQo0Vm1zUjA2cHB0dSs2bkN3RHlkN2dEOXhZbXZnWjEwUVM0SSs0NVMvYVc0elorTHBJK2VQNldpMnhyTzRRN01UCkpGK1cyZTJwaVBnQUduUEtGQkYvS3hQYVc1M084cHFFamlqVHpQbk5RdkNTdktsUzR3blFYRlUxNCt3cHBFazEKVXNRakFyOW42K0FVNDc1NFk5ZVArclh0VTJzRFJBQXhYWEVDck05dExJS21FTmtTMXl6UWVzOUJVVkhxQ1N2MQpDcHdsR0owQ0ppd3d5WmlvR1dmbmp5MEtHMFo2cHhIQkFhem1aVmUwSzUyUmRFY3RJYU8rQ25YcjBkWm5abG9vCmJVTVJQZ3ArenpnclhJMURSMU5OQTZjY1ZEaklGQmNUTkpjVFlTZWFvcjlIK1V4QVR2Z2kxL2ViY0J6amgrNlQKTW9uOUg4Zk5KbXgwT0tkOWVPR0dmMUlSSGdYZ25Wc3o5RHhaRmZLYTBsNUR3aWd6QVQzNW5wdmxPSDhhazR4RgoxTUpMakNzZHppZDJrbFpJQ0ZzWEFUVWRwK1Fydyt5aXVBQUMzelJHRnFwMTk0bXIwOFpxSXhybDlmSVhGK1RjCmdTNXR5Zllrb2I2dzB3U2tZcG9mdHJQNkF5aGZJUmN1ZnJxYjN3cVFjVmFJeHc2ZythQkJwaXBjTGo5RUVqaFYKQklxWmVqLzg4SWNRajZLMjVIYnV5MDFBT3VUSHZHMXJjaWFGTU5qcUR0Q2Z1aEkra1Evd2lJK2l4MVpCendaawpHQXhTZk1vVzV0dXhIcDVFV0hkRW8wZjEzQnZ3a3dNaGxWdVkvL0lNZDh4alZCYkdId3g0cFI5aFY5eUJoVzhyCkVPeTF1SUNBR1VUbmxVQjdKSWJqSlcyemZSZFFmNjJMUlpoZk1QMGpQV0ZoV3pTTVpZQWgvQkhGUzZIaGZ6aEoKaUIzQ1NOSHp5TXJTSnN2L3puRFVqZ3JRZUxGc0d1djFaT1ZZYmI5empBdThZMjY2dzFTQnU2SlExRnJobFFoRQorZkpzOCtXUFk0SEF3bHBTSG5OcTRrK1NmcCtVNUxZRERCUjhWSnVKa0hJS3RiTDc2R2V6UEFKUEk5amdiNmNCCjU3eHpLdmhVanh1TGhzQlcvNi9KMERmdHN4RHRuRTBsaXVjYjF6Q2tIWnptcHRjbUZOQTlodnp2a2hObXFWeXEKYXZ4ODFvejgxM3FmaXJkZ2EycmlPd2hQZ0l1di8rZWlEWEhHL21mSC9uTzU0MnFHZm5BVm5XYmd0dWxrSHp1VQpDN1NYN0RuTVZVTTh4RWdCQVJkdVNHRG5iMm11ZEhMbnpvNGtHeFovU3dTL05jUEFSWmxHQjFCM3dTazBsdUZRCnVjelc0N0k0VzUydFgrakVMRmFycnF6dTQ5ODdTREgxci9mb1BERjVKdXdWU0RKWVhIVk8zWENmL0Q0UDM0WkwKWFlaZkcxNTRrcVYxZk5JOGdvQ2Q3SUNYM2MzUnZIS3RISmQ3bHNCa2ZieWFINVpHTWhqSkd0amEzakVoK1JIWAo3MHFzaXhObGJJbTladTlXNUZYTldJWWo2VFo2M2VKaVYrZjBFMVU4dW5IeWRiZ2g0T0RzKzFpNVAvNE0zc2V0ClBwTmtSODUxaWRhd0FDd0xuQU1DR3pTZGUrRExrZnI4cUxDNm15U0xaQmIxRmtMQWlSS0VXVUV0WUp1TDVHTFEKeHpoblF1alJjMVA2aHpNMnUzZi9uc3FCUURka08rYys5UG1ybHIvdDhpMG5tTUhtMzduUklxKzNWS3N3WG5PeQpRZ3FEUmYzbjZGd290bDJweGFLTFhSc1Z2cDNWSUo3bXZqTkVxVHlTVmVsQ2pBam9oZlpPaWRaRVJ4U1NOLzBpCm1VMDBmMFpBTWw2Q3FBai9XK0s2L0pHV0RsNFVaUkZqNUo1MWh3ZkJTSktZNnBibDBBNDNpTVdwdnppeGVCeEsKUDJSMXlZRXhjZkFqMHZlY3pLZnBOOTZMNDB4WlVnMEVvVTVWU2pwL2lLQ21YTk5vMGloTkMwcW1YdjQwWGpQbQoraGVWSzZ5eElaL2pvVUR2S1hNWkM3Mit3U1pTQTRGVkJ6KzF5cjgxL0s4bXFvVTZWTmh1VzJnblNjYVNFQ1BpCnc2TUo2VUpJTHNNWmxxSHlBZ01lcW45c3pvM3diVWpuQiswSzZCZUhsVTE3TVZ6QzFoVlk5RHRmL2RBZFRxdmoKclQ2dytiY0Flb3g3OTQzaXh5QXVubGFIVDVBWGswWitRTUFIYWFOenQrR0p1YXEzVXQzQWhBaHl6SFBzblNEUgpBTXFwWGM0aEpTNmVvR1Zhc0hXYzdmYldUZz09Ci0tLS0tRU5EIEVOQ1JZUFRFRCBQUklWQVRFIEtFWS0tLS0tCi0tLS0tQkVHSU4gQ0VSVElGSUNBVEUtLS0tLQpNSUlGYWpDQ0ExSUNDUURVeWs0WmtVb3VaekFOQmdrcWhraUc5dzBCQVFzRkFEQjNNUXN3Q1FZRFZRUUdFd0pVClJURUxNQWtHQTFVRUNBd0NURXd4RFRBTEJnTlZCQWNNQkZSRlUxUXhEVEFMQmdOVkJBb01CRlJGVTFReERUQUwKQmdOVkJBc01CRlJGVTFReERUQUxCZ05WQkFNTUJGUkZVMVF4SHpBZEJna3Foa2lHOXcwQkNRRVdFRzV2ZDJobApjbVZBZEdWemRDNWpiMjB3SGhjTk1qSXdPREkxTVRFeU9UTTNXaGNOTWpNd09ESTFNVEV5T1RNM1dqQjNNUXN3CkNRWURWUVFHRXdKVVJURUxNQWtHQTFVRUNBd0NURXd4RFRBTEJnTlZCQWNNQkZSRlUxUXhEVEFMQmdOVkJBb00KQkZSRlUxUXhEVEFMQmdOVkJBc01CRlJGVTFReERUQUxCZ05WQkFNTUJGUkZVMVF4SHpBZEJna3Foa2lHOXcwQgpDUUVXRUc1dmQyaGxjbVZBZEdWemRDNWpiMjB3Z2dJaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQ0R3QXdnZ0lLCkFvSUNBUUM4NXRiNmVkYXJsKytJNHRBbmh2bkZocXZKNEV6UDVWY0Jib051ZTBmN2s2NzRQVDd2OFJVOTdmdlIKc0pMM1ZjZmVpajhkUTBlNTVUMzVxZEdnVVFTNm95L0ZNUUZiY2tzRmVmcEVMRWZiUkp5UHVsaGtOVnZpU2hWYgozUXBRa2dVTVF6NnJteFBQSUg1bXhVRlJoNVc5QUpsWENZQjBCdGtBYWdOb05LWWhxYVRlUWh4bkM4YXMweTdOCmsvODJSVnY2M2NDQzYrZ1NkWkxBYUJCMWxhK09UV1hyK3NaMzRkZlBnKzI4amM3SE1aaUlaOHlacm5IQ0llakkKVjYwQk1rUHNEQ0VNMWxDQUlvbDVWeDV6QThkU1BRWEh2MWNTNmtUaVcwdHJ2V1NDTUNORDlUT3RJeEt1Q2djYwp4UlQzZ1YzY1NQNGJheGJ3QjJqYTVTeEhuOWdPWS9KbmJSaGNtMlB0czBDbUZxS2FHR056RDBYM2ZFNm91c0huClczSlZWMytyWjlYR25namF3RmhwbGNycm9acUp2dHR2VFplK1kvbytrRk5kek52U2lWMmVodkQzMGdiTmJZM1kKcHlUaTJXam1DbS93cFoyakIySkdQaDF2cXZVLzdTcjc0WmdYV2oyT1R2eEtuZWNLckRLRlVwQWc4N0k4OXBMUApDS2E4NjYwQlY2aS92NDlWVERXbWVjUXN6OVkxR1YvOHd3OHNMdFBWOVZwdUFody9OZmR5a0VaTVNYbzJSZ2hBCmtKRzFVdmtlZVNnRWFJSDdsTEVVYVY0MGFyY0VDVGMzMjQzUnBjQXU3Qy9JcmtqUjNuL09XSWlmV0VNOXM2WnQKVytKTW1lNDBKOGRWaWtyRm8vbWttQXY0a3hndm4yeVZOSTVJQ3FIRTVDMWN1OGUrYndJREFRQUJNQTBHQ1NxRwpTSWIzRFFFQkN3VUFBNElDQVFCYmlxc3p2d3N5Z3kzaWw3QnplWld3ckYreTFGd2lOZXpNb09lNHd4ZW94Tk9hCmYzd2JTdTJSU0NBRXIrVUVLWExjaXZUWTc3alhXdWxZTTJQNFhZNTc2L0RHci8yQmFsNFFZMHRrSVA5NVcxcjkKbndGcjFySVo4OHBLalpTR1Iyd1hGL2J0bHhIa081M3ROOC94aDZXNi83azBUNHNHZGxTSDNhOE1zTFRFMzl6OApDUUY3WXAwWUl4OEltam5aQk9PQXlJb0lITDdvVVBiRDR6Rk9yc2hpMjlmMEdhQ0pyTStJalBKRkY0bGR4M2RaCjBadWFCT0JNZkxLeUVteVQ3b3ZtSG4zZjRpNnZxQldJTTBpaFpqdGhQZGJpZTJFQi9VZDU2ZHl3SmNZdWV4TWIKNXZLUFh5dDBUbGlRcWRiaGFkL2xIYnpqaUdITTVVU1VwK0NkTk13c3JtSkw1NFdEMUk1emtOeTBUbEZZUjVGSQpUMXdtSVZVclM4WTFtUk1tQ09XUE1mMmRkSlZ1K0ZUS25JdGlJeURwQUpoaHlCcG5FcThXRnpBYzFlODZTOUZMCm93KzJtUkViMk1QTS9OdmEzZWRNTFViN1g3UXZaUVZLbGdaRmJrN0crNEJsSEkvcW9jdUt1N1EyZXFCYkxEeCsKd2J6bGpYVzNlWWc5U3VsQkVmeUl4ZXJJQ1BWZ2N2QnZPWXNOS3dwdTZiNXBscjUzOXdlbmZrY0l1dWtnOGFvcwo1eUcxZEt3eWNFK0FOT2FHUkZwYTJ1V2g3SmxXYTVydVJ5R21pem5wTnN6ODlORDArNTBsQ2Ezcys2blZHa2pBCmFMVlMwV3FEM1ZEcmNRdUl3dkxhVnlUbVFsaFdpbWh6c2ZQSEdrZVY1RVo4c3Q4TlNCandWZWRuQjFNQU9nPT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQotLS0tLUJFR0lOIENFUlRJRklDQVRFLS0tLS0KTUlJRmFqQ0NBMUlDQ1FEVXlrNFprVW91WnpBTkJna3Foa2lHOXcwQkFRc0ZBREIzTVFzd0NRWURWUVFHRXdKVQpSVEVMTUFrR0ExVUVDQXdDVEV3eERUQUxCZ05WQkFjTUJGUkZVMVF4RFRBTEJnTlZCQW9NQkZSRlUxUXhEVEFMCkJnTlZCQXNNQkZSRlUxUXhEVEFMQmdOVkJBTU1CRlJGVTFReEh6QWRCZ2txaGtpRzl3MEJDUUVXRUc1dmQyaGwKY21WQWRHVnpkQzVqYjIwd0hoY05Nakl3T0RJMU1URXlPVE0zV2hjTk1qTXdPREkxTVRFeU9UTTNXakIzTVFzdwpDUVlEVlFRR0V3SlVSVEVMTUFrR0ExVUVDQXdDVEV3eERUQUxCZ05WQkFjTUJGUkZVMVF4RFRBTEJnTlZCQW9NCkJGUkZVMVF4RFRBTEJnTlZCQXNNQkZSRlUxUXhEVEFMQmdOVkJBTU1CRlJGVTFReEh6QWRCZ2txaGtpRzl3MEIKQ1FFV0VHNXZkMmhsY21WQWRHVnpkQzVqYjIwd2dnSWlNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0SUNEd0F3Z2dJSwpBb0lDQVFDODV0YjZlZGFybCsrSTR0QW5odm5GaHF2SjRFelA1VmNCYm9OdWUwZjdrNjc0UFQ3djhSVTk3ZnZSCnNKTDNWY2ZlaWo4ZFEwZTU1VDM1cWRHZ1VRUzZveS9GTVFGYmNrc0ZlZnBFTEVmYlJKeVB1bGhrTlZ2aVNoVmIKM1FwUWtnVU1RejZybXhQUElINW14VUZSaDVXOUFKbFhDWUIwQnRrQWFnTm9OS1locWFUZVFoeG5DOGFzMHk3TgprLzgyUlZ2NjNjQ0M2K2dTZFpMQWFCQjFsYStPVFdYcitzWjM0ZGZQZysyOGpjN0hNWmlJWjh5WnJuSENJZWpJClY2MEJNa1BzRENFTTFsQ0FJb2w1Vng1ekE4ZFNQUVhIdjFjUzZrVGlXMHRydldTQ01DTkQ5VE90SXhLdUNnY2MKeFJUM2dWM2NTUDRiYXhid0IyamE1U3hIbjlnT1kvSm5iUmhjbTJQdHMwQ21GcUthR0dOekQwWDNmRTZvdXNIbgpXM0pWVjMrclo5WEduZ2phd0ZocGxjcnJvWnFKdnR0dlRaZStZL28ra0ZOZHpOdlNpVjJlaHZEMzBnYk5iWTNZCnB5VGkyV2ptQ20vd3BaMmpCMkpHUGgxdnF2VS83U3I3NFpnWFdqMk9UdnhLbmVjS3JES0ZVcEFnODdJODlwTFAKQ0thODY2MEJWNmkvdjQ5VlREV21lY1FzejlZMUdWLzh3dzhzTHRQVjlWcHVBaHcvTmZkeWtFWk1TWG8yUmdoQQprSkcxVXZrZWVTZ0VhSUg3bExFVWFWNDBhcmNFQ1RjMzI0M1JwY0F1N0MvSXJralIzbi9PV0lpZldFTTlzNlp0ClcrSk1tZTQwSjhkVmlrckZvL21rbUF2NGt4Z3ZuMnlWTkk1SUNxSEU1QzFjdThlK2J3SURBUUFCTUEwR0NTcUcKU0liM0RRRUJDd1VBQTRJQ0FRQmJpcXN6dndzeWd5M2lsN0J6ZVpXd3JGK3kxRndpTmV6TW9PZTR3eGVveE5PYQpmM3diU3UyUlNDQUVyK1VFS1hMY2l2VFk3N2pYV3VsWU0yUDRYWTU3Ni9ER3IvMkJhbDRRWTB0a0lQOTVXMXI5Cm53RnIxcklaODhwS2paU0dSMndYRi9idGx4SGtPNTN0TjgveGg2VzYvN2swVDRzR2RsU0gzYThNc0xURTM5ejgKQ1FGN1lwMFlJeDhJbWpuWkJPT0F5SW9JSEw3b1VQYkQ0ekZPcnNoaTI5ZjBHYUNKck0rSWpQSkZGNGxkeDNkWgowWnVhQk9CTWZMS3lFbXlUN292bUhuM2Y0aTZ2cUJXSU0waWhaanRoUGRiaWUyRUIvVWQ1NmR5d0pjWXVleE1iCjV2S1BYeXQwVGxpUXFkYmhhZC9sSGJ6amlHSE01VVNVcCtDZE5Nd3NybUpMNTRXRDFJNXprTnkwVGxGWVI1RkkKVDF3bUlWVXJTOFkxbVJNbUNPV1BNZjJkZEpWdStGVEtuSXRpSXlEcEFKaGh5QnBuRXE4V0Z6QWMxZTg2UzlGTApvdysybVJFYjJNUE0vTnZhM2VkTUxVYjdYN1F2WlFWS2xnWkZiazdHKzRCbEhJL3FvY3VLdTdRMmVxQmJMRHgrCndiemxqWFczZVlnOVN1bEJFZnlJeGVySUNQVmdjdkJ2T1lzTkt3cHU2YjVwbHI1Mzl3ZW5ma2NJdXVrZzhhb3MKNXlHMWRLd3ljRStBTk9hR1JGcGEydVdoN0psV2E1cnVSeUdtaXpucE5zejg5TkQwKzUwbENhM3MrNm5WR2tqQQphTFZTMFdxRDNWRHJjUXVJd3ZMYVZ5VG1RbGhXaW1oenNmUEhHa2VWNUVaOHN0OE5TQmp3VmVkbkIxTUFPZz09Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0KLS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUZhakNDQTFJQ0NRRFV5azRaa1VvdVp6QU5CZ2txaGtpRzl3MEJBUXNGQURCM01Rc3dDUVlEVlFRR0V3SlUKUlRFTE1Ba0dBMVVFQ0F3Q1RFd3hEVEFMQmdOVkJBY01CRlJGVTFReERUQUxCZ05WQkFvTUJGUkZVMVF4RFRBTApCZ05WQkFzTUJGUkZVMVF4RFRBTEJnTlZCQU1NQkZSRlUxUXhIekFkQmdrcWhraUc5dzBCQ1FFV0VHNXZkMmhsCmNtVkFkR1Z6ZEM1amIyMHdIaGNOTWpJd09ESTFNVEV5T1RNM1doY05Nak13T0RJMU1URXlPVE0zV2pCM01Rc3cKQ1FZRFZRUUdFd0pVUlRFTE1Ba0dBMVVFQ0F3Q1RFd3hEVEFMQmdOVkJBY01CRlJGVTFReERUQUxCZ05WQkFvTQpCRlJGVTFReERUQUxCZ05WQkFzTUJGUkZVMVF4RFRBTEJnTlZCQU1NQkZSRlUxUXhIekFkQmdrcWhraUc5dzBCCkNRRVdFRzV2ZDJobGNtVkFkR1Z6ZEM1amIyMHdnZ0lpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElDRHdBd2dnSUsKQW9JQ0FRQzg1dGI2ZWRhcmwrK0k0dEFuaHZuRmhxdko0RXpQNVZjQmJvTnVlMGY3azY3NFBUN3Y4UlU5N2Z2UgpzSkwzVmNmZWlqOGRRMGU1NVQzNXFkR2dVUVM2b3kvRk1RRmJja3NGZWZwRUxFZmJSSnlQdWxoa05WdmlTaFZiCjNRcFFrZ1VNUXo2cm14UFBJSDVteFVGUmg1VzlBSmxYQ1lCMEJ0a0FhZ05vTktZaHFhVGVRaHhuQzhhczB5N04Kay84MlJWdjYzY0NDNitnU2RaTEFhQkIxbGErT1RXWHIrc1ozNGRmUGcrMjhqYzdITVppSVo4eVpybkhDSWVqSQpWNjBCTWtQc0RDRU0xbENBSW9sNVZ4NXpBOGRTUFFYSHYxY1M2a1RpVzB0cnZXU0NNQ05EOVRPdEl4S3VDZ2NjCnhSVDNnVjNjU1A0YmF4YndCMmphNVN4SG45Z09ZL0puYlJoY20yUHRzMENtRnFLYUdHTnpEMFgzZkU2b3VzSG4KVzNKVlYzK3JaOVhHbmdqYXdGaHBsY3Jyb1pxSnZ0dHZUWmUrWS9vK2tGTmR6TnZTaVYyZWh2RDMwZ2JOYlkzWQpweVRpMldqbUNtL3dwWjJqQjJKR1BoMXZxdlUvN1NyNzRaZ1hXajJPVHZ4S25lY0tyREtGVXBBZzg3STg5cExQCkNLYTg2NjBCVjZpL3Y0OVZURFdtZWNRc3o5WTFHVi84d3c4c0x0UFY5VnB1QWh3L05mZHlrRVpNU1hvMlJnaEEKa0pHMVV2a2VlU2dFYUlIN2xMRVVhVjQwYXJjRUNUYzMyNDNScGNBdTdDL0lya2pSM24vT1dJaWZXRU05czZadApXK0pNbWU0MEo4ZFZpa3JGby9ta21BdjRreGd2bjJ5Vk5JNUlDcUhFNUMxY3U4ZStid0lEQVFBQk1BMEdDU3FHClNJYjNEUUVCQ3dVQUE0SUNBUUJiaXFzenZ3c3lneTNpbDdCemVaV3dyRit5MUZ3aU5lek1vT2U0d3hlb3hOT2EKZjN3YlN1MlJTQ0FFcitVRUtYTGNpdlRZNzdqWFd1bFlNMlA0WFk1NzYvREdyLzJCYWw0UVkwdGtJUDk1VzFyOQpud0ZyMXJJWjg4cEtqWlNHUjJ3WEYvYnRseEhrTzUzdE44L3hoNlc2LzdrMFQ0c0dkbFNIM2E4TXNMVEUzOXo4CkNRRjdZcDBZSXg4SW1qblpCT09BeUlvSUhMN29VUGJENHpGT3JzaGkyOWYwR2FDSnJNK0lqUEpGRjRsZHgzZFoKMFp1YUJPQk1mTEt5RW15VDdvdm1IbjNmNGk2dnFCV0lNMGloWmp0aFBkYmllMkVCL1VkNTZkeXdKY1l1ZXhNYgo1dktQWHl0MFRsaVFxZGJoYWQvbEhiemppR0hNNVVTVXArQ2ROTXdzcm1KTDU0V0QxSTV6a055MFRsRllSNUZJClQxd21JVlVyUzhZMW1STW1DT1dQTWYyZGRKVnUrRlRLbkl0aUl5RHBBSmhoeUJwbkVxOFdGekFjMWU4NlM5RkwKb3crMm1SRWIyTVBNL052YTNlZE1MVWI3WDdRdlpRVktsZ1pGYms3Rys0QmxISS9xb2N1S3U3UTJlcUJiTER4Kwp3YnpsalhXM2VZZzlTdWxCRWZ5SXhlcklDUFZnY3ZCdk9Zc05Ld3B1NmI1cGxyNTM5d2VuZmtjSXV1a2c4YW9zCjV5RzFkS3d5Y0UrQU5PYUdSRnBhMnVXaDdKbFdhNXJ1UnlHbWl6bnBOc3o4OU5EMCs1MGxDYTNzKzZuVkdrakEKYUxWUzBXcUQzVkRyY1F1SXd2TGFWeVRtUWxoV2ltaHpzZlBIR2tlVjVFWjhzdDhOU0Jqd1ZlZG5CMU1BT2c9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==";
    public static final String TEST_KEYSTORE_CONTENT_P12 = "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";
    public static final IRetryPolicy TEST_RETRY_POLICY = new SimpleRetryPolicy();
    public static final Set<String> TEST_CONDITIONS = singleton("TEST_CONDITION");
    public static final HttpClient TEST_HTTP_CLIENT = Mockito.mock(HttpClient.class);
    public static final URI TEST_OAUTH_SERVICE_URI = URI.create("https://nowhere.near.a.real.oauth.uri.sap.com");
    public static final Map<String, String> TEST_PROPERTIES = singletonMap("TEST_PROPERTY_KEY", "TEST_PROPERTY_VALUE");
    public static final Condition TEST_CONDITION = new Condition(TEST_NAME, TEST_DESCRIPTION, TEST_PROPERTY_KEY, TEST_PREDICATE, TEST_PROPERTY_VALUE, TEST_MANDATORY, TEST_LABELS);
    public static final String HTTP_CERTIFICATE_CREDENTIALS_RESPONSE_FORMAT = "{\"destinationConfiguration\": {\"Authentication\": \"ClientCertificateAuthentication\",\"URL\": \"https://nowhere.near.a.real.uri.sap.com\", \"KeyStorePassword\": \"test\"},\"certificates\":[{\"Name\":\"test.p12\",\"Content\":\"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\",\"Type\":\"CERTIFICATE\"}]}";
    public static final String HTTP_BASIC_AUTHORIZATION_RESPONSE_FORMAT = "{\"destinationConfiguration\": {\"Authentication\": \"BasicAuthentication\",\"URL\": \"https://nowhere.near.a.real.uri.sap.com\", \"User\": \"username\", \"Password\": \"password\"}}";
    public static final String HTTP_OAUTH_AUTHORIZATION_RESPONSE_FORMAT = "{\"destinationConfiguration\": {\"Authentication\": \"OAuth2ClientCredentials\",\"URL\": \"https://nowhere.near.a.real.uri.sap.com\", \"clientId\": \"username\", \"clientSecret\": \"password\", \"tokenServiceURL\": \"https://nowhere.near.a.real.uri.sap.com/oauth/token?grant_type=client_credentials\"}}";
    public static final PagingMetadata TEST_CONFIGURATION_PAGING_METADATA = new PagingMetadata(TEST_PAGE, TEST_PAGE_SIZE, TEST_TOTAL_PAGES, TEST_TOTAL_RESULTS_COUNT);
    public static final ServiceRegion TEST_SERVICE_REGION = ServiceRegion.EU10;
    public static final String TEST_SERVICE_REGION_URI = "https://nowhere.near.a.real.uri.sap.com";
    public static final ServiceRegion TEST_DESTINATION_SERVICE_REGION = new ServiceRegion(Platform.CF, TEST_SERVICE_REGION_URI);
    public static final URI TEST_SERVICE_URI = TEST_SERVICE_REGION.getServiceURI();
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(NON_NULL);
    public static final TypeReference<ConfigurationResponse<Condition>> CONDITION_CONFIGURATION_TYPE = new TypeReference<ConfigurationResponse<Condition>>() {

    };

    public static <T> T fromJsonString(String valueAsString, Class<T> clazz) {
        try {
            return JSON_OBJECT_MAPPER.readValue(valueAsString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String valueAsString, TypeReference<T> typeReference) {
        try {
            return JSON_OBJECT_MAPPER.readValue(valueAsString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toJsonString(T value) {
        try {
            return JSON_OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T extractFieldValue(Object instance, String fieldName) {
        try {
            Field declaredField = instance.getClass().getDeclaredField(fieldName);

            declaredField.setAccessible(Boolean.TRUE);

            return (T) declaredField.get(instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T extractSuperClassFieldValue(Object instance, String fieldName) {
        try {
            Field declaredField = instance.getClass().getSuperclass().getDeclaredField(fieldName);

            declaredField.setAccessible(Boolean.TRUE);

            return (T) declaredField.get(instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse createOAuthHttpResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(TEST_TEST_OAUTH_RESPONSE.getBytes(UTF_8)));

        return response;
    }

    public static HttpResponse createCertificateAuthenticationResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(HTTP_CERTIFICATE_CREDENTIALS_RESPONSE_FORMAT.getBytes(UTF_8)));

        return response;
    }

    public static HttpResponse createBasicAuthenticationHeaderResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(HTTP_BASIC_AUTHORIZATION_RESPONSE_FORMAT.getBytes(UTF_8)));

        return response;
    }

    public static HttpResponse createOauthAuthenticationHeaderResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(HTTP_OAUTH_AUTHORIZATION_RESPONSE_FORMAT.getBytes(UTF_8)));

        return response;
    }
}
