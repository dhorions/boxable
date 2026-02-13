# Release Process

Starting from version 1.7.1, all releases are automatically published to GitHub with auto-generated release notes. When a new version tag is pushed to the repository, a GitHub Actions workflow automatically:

1. Creates a GitHub Release with the version tag
2. Generates release notes from commit history since the previous release
3. Links to the full changelog and dependency information

## Creating a New Release

To create a new release:

1. Update the version in `pom.xml`
2. Commit and push your changes
3. Create and push a version tag (e.g., `git tag 1.8.3 && git push origin 1.8.3`)
4. The GitHub Actions workflow will automatically create the release

For Maven Central deployment, use the existing "Publish to Maven Central" workflow action.

## Historical Releases

For versions that were published to Maven Central but don't have GitHub Releases (1.7.3, 1.8.0, 1.8.1, 1.8.2), two workflows are available:

1. **Create All Historical Releases** - Automatically creates releases for all missing historical versions at once
2. **Create Retroactive Release** - Manually create a release for a specific historical version by providing the version number

These workflows can be triggered from the Actions tab in the GitHub repository.

**Note:** Since the exact commit states for these historical versions are not available in this repository, the tags and releases will be created at the current master branch. The actual released code for these versions is available on [Maven Central](https://central.sonatype.com/artifact/com.github.dhorions/boxable).

## Automated Release Workflows

### For Future Releases (`.github/workflows/create-release.yml`)
This workflow automatically triggers when version tags are pushed to the repository and:
- Uses semantic version comparison to filter versions after 1.7.0
- Auto-generates release notes from commit history
- Includes links to dependencies and full changelog
- Handles pre-release versions (tags with `-` suffix)

### For Historical Releases (`.github/workflows/create-all-historical-releases.yml`)
This workflow can be manually triggered to create releases for all missing historical versions:
- Creates releases for 1.7.3, 1.8.0, 1.8.1, and 1.8.2 in parallel
- Creates tags at current master HEAD with clear warnings about limitations
- Generates release notes with Maven Central links

### For Single Retroactive Releases (`.github/workflows/create-retroactive-release.yml`)
This workflow can be manually triggered for any specific version:
- Accepts version number and previous version as inputs
- Creates tags and releases if they don't exist
- Flexible for any future retroactive releases needed
