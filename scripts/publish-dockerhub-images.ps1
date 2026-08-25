param(
    [Parameter(Mandatory = $true)]
    [string]$DockerHubNamespace,

    [string]$DevTag = "v1.0.0",
    [string]$ProdTag = "v1.0.0",

    [switch]$SkipPush
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot

$javaServices = @(
    @{ Name = "api-gateway"; Module = "api-gateway"; Port = "8080" },
    @{ Name = "user-service"; Module = "user-service"; Port = "8700" },
    @{ Name = "product-service"; Module = "product-service"; Port = "8500" },
    @{ Name = "order-service"; Module = "order-service"; Port = "8300" },
    @{ Name = "payment-service"; Module = "payment-service"; Port = "8400" },
    @{ Name = "shipping-service"; Module = "shipping-service"; Port = "8600" },
    @{ Name = "favourite-service"; Module = "favourite-service"; Port = "8800" }
)

$allImages = @(
    @{ Name = "frontend"; Build = { param($tag)
            docker build `
                -t "$DockerHubNamespace/frontend:$tag" `
                -f "$repoRoot/frontend/Dockerfile" `
                "$repoRoot/frontend"
        }
    }
) + ($javaServices | ForEach-Object {
    $service = $_
    $serviceName = $service.Name
    $moduleName = $service.Module
    $modulePort = $service.Port
    @{
        Name = $serviceName
        Build = {
            param($tag)
            docker build `
                -t "${DockerHubNamespace}/${serviceName}:$tag" `
                -f "$repoRoot/docker/java-service.Dockerfile" `
                --build-arg "MODULE_NAME=$moduleName" `
                --build-arg "MODULE_PORT=$modulePort" `
                "$repoRoot"
        }.GetNewClosure()
    }
})

function Assert-LastCommandSucceeded {
    param(
        [string]$Action
    )

    if ($LASTEXITCODE -ne 0) {
        throw "$Action failed with exit code $LASTEXITCODE."
    }
}

function Invoke-BuildAndMaybePush {
    param(
        [string]$Tag
    )

    foreach ($image in $allImages) {
        Write-Host "Building $($image.Name):$Tag"
        & $image.Build $Tag
        Assert-LastCommandSucceeded -Action "Build for $($image.Name):$Tag"

        if (-not $SkipPush) {
            Write-Host "Pushing $DockerHubNamespace/$($image.Name):$Tag"
            docker push "$DockerHubNamespace/$($image.Name):$Tag"
            Assert-LastCommandSucceeded -Action "Push for $($image.Name):$Tag"
        }
    }
}

function Set-KustomizationImages {
    param(
        [string]$Path,
        [string]$Tag
    )

    $content = Get-Content $Path -Raw
    $serviceNames = @('frontend', 'api-gateway', 'user-service', 'product-service', 'order-service', 'payment-service', 'shipping-service', 'favourite-service')

    foreach ($serviceName in $serviceNames) {
        # Preserve the base image as the match key and replace only this image
        # entry's newName/newTag values. This keeps repeated publishes stable.
        $pattern = "(?ms)(- name:\s*)(?:ghcr\.io/your-org|docker\.io/[^/]+)/$([regex]::Escape($serviceName))[^\r\n]*(\r?\n\s*newName:\s*)[^\r\n]*(\r?\n\s*newTag:\s*)[^\r\n]+"
        $replacement = "`${1}ghcr.io/your-org/$serviceName`${2}docker.io/$DockerHubNamespace/$serviceName`${3}$Tag"
        $content = [regex]::Replace($content, $pattern, $replacement)
    }

    Set-Content -Path $Path -Value $content -NoNewline
}

Invoke-BuildAndMaybePush -Tag $DevTag

# A shared tag identifies the same immutable release; rebuilding it only adds
# time and can make the dev and prod images differ unexpectedly.
if ($ProdTag -ne $DevTag) {
    Invoke-BuildAndMaybePush -Tag $ProdTag
}

Set-KustomizationImages -Path "$repoRoot/deploy/k8s/overlays/dev/kustomization.yaml" -Tag $DevTag
Set-KustomizationImages -Path "$repoRoot/deploy/k8s/overlays/prod/kustomization.yaml" -Tag $ProdTag

Write-Host ""
Write-Host "Done."
Write-Host "Updated overlays to use docker.io/$DockerHubNamespace/* images."
Write-Host "Services published:"
Write-Host "  - frontend"
Write-Host "  - api-gateway"
Write-Host "  - user-service"
Write-Host "  - product-service"
Write-Host "  - order-service"
Write-Host "  - payment-service"
Write-Host "  - shipping-service"
Write-Host "  - favourite-service"
Write-Host "Dev tag:  $DevTag"
Write-Host "Prod tag: $ProdTag"
