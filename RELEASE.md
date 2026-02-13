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

1. **Create All Historical Releases** - Automatically creates releases for all missing historical versions at once (creates tags at current master)
2. **Create Retroactive Release** - Manually create a release for a specific historical version with the option to specify the exact git reference (commit SHA, branch, or tag)

These workflows can be triggered from the Actions tab in the GitHub repository.

**Creating Accurate Historical Releases:**
If you know the exact commit SHA where a historical version was released, use the "Create Retroactive Release" workflow and provide:
- The version number (e.g., `1.8.0`)
- The previous version (e.g., `1.7.3`)
- The git reference (commit SHA, branch, or tag) where the release was made

This will create the tag at the correct historical point and generate accurate release notes based on the actual commits included in that release.

**Note:** If the exact commit states for historical versions are not available, tags will be created at the current master branch. The actual released code for these versions is available on [Maven Central](https://central.sonatype.com/artifact/com.github.dhorions/boxable).

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
- **Accepts an optional git reference (commit SHA, branch, or tag)** to create the tag at the correct historical point
- If no git reference is provided, creates the tag at current master
- Generates accurate release notes based on commits between the previous version and the specified reference
- Creates tags and releases if they don't exist
- Flexible for any future retroactive releases needed

**Example Usage:**
- To create a release at a specific commit: Provide the commit SHA (e.g., `a1b2c3d`)
- To create a release at a branch state: Provide the branch name (e.g., `release/1.8.0`)
- To create a release at an existing tag: Provide the tag name (e.g., `v1.8.0-maven-release`)
- To create a release at current master: Leave the git reference field empty
