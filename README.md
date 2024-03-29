# Port Knocker - Android

[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/packages/ca.andries.portknocker/)

Port knocking utility application for Android.

## Features

- Ability to save/use profiles
- Supports one time sequences, automatically rotates port sequence when sequence list is provided
- Stores knock history
- Ability to check open port after knocking, to check for a successful knock

## Screenshots

<table>
<tr>
<td><img src="doc/screen1.png"></td>
<td><img src="doc/screen2.png"></td>
<td><img src="doc/screen3.png"></td>
</tr>
</table>

## Behavior notes

- When using one time sequences and open port checking, sequence rotation will only occur if the port knock was considered successful.
- If using the one time sequence feature, the next port sequence will be shown between in the parentheses in the profile list.
- The connect timeout for each port knock is 1000 ms.
- History will store the last 100 knocks
