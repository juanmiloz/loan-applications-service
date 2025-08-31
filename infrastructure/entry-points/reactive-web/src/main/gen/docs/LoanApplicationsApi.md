# LoanApplicationsApi

All URIs are relative to *https://api.your-domain.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createLoanApplication**](LoanApplicationsApi.md#createLoanApplication) | **POST** /api/v1/loan-application | Create a loan application |


<a id="createLoanApplication"></a>
# **createLoanApplication**
> LoanApplicationDTO createLoanApplication(createLoanApplicationDTO)

Create a loan application

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.LoanApplicationsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.your-domain.com");

    LoanApplicationsApi apiInstance = new LoanApplicationsApi(defaultClient);
    CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO(); // CreateLoanApplicationDTO | 
    try {
      LoanApplicationDTO result = apiInstance.createLoanApplication(createLoanApplicationDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling LoanApplicationsApi#createLoanApplication");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **createLoanApplicationDTO** | [**CreateLoanApplicationDTO**](CreateLoanApplicationDTO.md)|  | |

### Return type

[**LoanApplicationDTO**](LoanApplicationDTO.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created |  * Location - URL of the created resource <br>  |
| **400** | Invalid request (validation error) |  -  |
| **409** | Conflict (e.g., duplicate request) |  -  |
| **500** | Internal server error |  -  |

