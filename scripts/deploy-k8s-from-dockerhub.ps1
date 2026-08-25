param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("dev", "prod")]
    [string]$Environment,

    [switch]$IncludeAddons
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$overlayPath = Join-Path $repoRoot "deploy\k8s\overlays\$Environment"
$namespace = "ecommerce-$Environment"
$migrationJobs = @(
    "user-service-migration",
    "product-service-migration",
    "favourite-service-migration",
    "shipping-service-migration",
    "order-service-migration",
    "payment-service-migration"
)

function Remove-MigrationJobs {
    foreach ($job in $migrationJobs) {
        $existingJob = kubectl get job $job -n $namespace --ignore-not-found -o name 2>$null
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to check existing job '$job' in namespace '$namespace'."
        }

        if ($existingJob) {
            Write-Host "Deleting immutable migration job: $job"
            kubectl delete job $job -n $namespace --wait=true
            if ($LASTEXITCODE -ne 0) {
                throw "Failed to delete migration job '$job' in namespace '$namespace'."
            }
        }
    }
}

Write-Host "Applying overlay: $overlayPath"
Remove-MigrationJobs
kubectl apply -k $overlayPath

if ($IncludeAddons) {
    $addonsPath = Join-Path $repoRoot "deploy\k8s\overlays\$Environment-addons"
    if (Test-Path $addonsPath) {
        Write-Host "Applying addons overlay: $addonsPath"
        kubectl apply -k $addonsPath
    }
}

Write-Host ""
Write-Host "Deployment submitted for environment: $Environment"
Write-Host "Check status with:"
Write-Host "  kubectl get deploy -n ecommerce-$Environment"
Write-Host "  kubectl get pods -n ecommerce-$Environment"
